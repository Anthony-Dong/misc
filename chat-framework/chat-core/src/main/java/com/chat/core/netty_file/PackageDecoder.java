package com.chat.core.netty_file;


import com.chat.core.model.NPack;
import com.chat.core.model.URL;
import com.chat.core.model.UrlConstants;
import com.chat.core.netty.Constants;
import com.chat.core.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解码器会很麻烦 (这个包含两个解码器. 一个是文件协议, 一个是普通文本协议)
 * <p>
 * 主要分为 4种情况
 * <p>
 * 1. 缓冲区只有一个数据包,此时只用做 版本校验 , 长度校验 , 然后读就可以了
 * 2. 缓冲区有多个数据包 , 可能是整数的倍数 , 就需要迭代读取
 * 3. 缓冲区可能有多个数据包 , 可能出现半个包的问题, 比如 2.5个 包, 此时就需要解码时注意
 * 4. 如果出现半个+整数个, 前面根本无法解码 , 此时就无法处理 , 可能出现丢包
 * <p>
 * 所以我们要求的是数据传输的完整性,最低要求将数据包完整的传输和接收
 *
 * @date:2019/11/10 13:40
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public final class PackageDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(PackageDecoder.class);
    /**
     * 保存文件的channel和stream. 防止大量申请资源
     */
    private Map<String, Pair<FileOutputStream, FileChannel>> file_map;

    /**
     * 一个临时对象,保存未被处理的对象
     */
    private ByteBuf cache;

    /**
     * 默认值是 {@link com.chat.core.netty.Constants#PROTOCOL_VERSION}
     */
    private final short VERSION;
    private static final MessagePack pack = new MessagePack();

    /**
     * 构造方法
     */
    public PackageDecoder(short version) {
        super();
        this.VERSION = version;
    }

    /**
     * {@link ByteToMessageDecoder#channelRead}
     * <p>
     * 解码器 , 记住in千万不要release操作.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 处理数据包...
        handler(in, out);

        // 处理完后,进行解析文件包
        if (cache == null || cache.refCnt() == 0) {
            handlerFile(ctx, in, cache, out);
        } else {
            System.out.println("使用cache");
            // 有缓存,写入in. 我们读取缓存内容. (缓存注意内存问题.)
            cache.writeBytes(in);
            // 释放buf , 这个in , ByteToMessageDecoder会帮助你释放.
            // 处理我们的cache., 但是我们需要释放cache
            handlerFile(ctx, null, cache, out);
        }
    }

    /**
     * 处理器 - 处理普通的协议
     */
    private void handler(ByteBuf in, List<Object> out) {
        // 如果可读
        while (in.isReadable()) {
            try {
                // 1. 记录最开始读取的位置 , 防止出错
                int release = in.readerIndex();

                // 2. 读取版本号 , 2个字节
                short version = in.readShort();

                // 3. 版本号一致 - > 继续执行
                if (version == VERSION) {

                    // 4. 解码 -> 操作
                    NPack read = null;
                    byte[] bytes = null;

                    try {
                        // 5. 读取长度 , 4个字节
                        int len = in.readInt();

                        // 6. 实例化数组
                        bytes = new byte[len];

                        // 7. 读取到数组中 , 此时可能会有异常 - > 我们抓住 indexOutOfBoundException
                        in.readBytes(bytes, 0, len);

                        // 8. 如果么问题, 就进行解码 -> 也可能出现异常 -> 抓取异常
                        read = pack.read(bytes, NPack.class);

                        // catch 抓取任何异常
                    } catch (Throwable e) {
                        // 不做任何处理
                    } finally {
                        // 清空数组引用 - 快速释放内存
                        bytes = null;
                    }
                    // 解码错误-> 重置读指针位置 -> 返回
                    if (read == null) {
                        in.readerIndex(release);
                        return;
                    } else {
                        // 一致就添加进去 - > 啥也不做
                        out.add(read);
                    }
                } else {
                    // 版本不一致 -> 重置读指针位置 -> 返回
                    in.readerIndex(release);
                    return;
                }
            } catch (Exception e) {
                // 读异常.直接return. 防止读取版本号都会异常.
                return;
            }
        }
    }


    /**
     * buf1传入的是in(ByteToMessage管的) , buf2传入的是cache.
     * 为了区分开
     */
    private void handlerFile(ChannelHandlerContext ctx, ByteBuf buf1, ByteBuf buf2, List<Object> out) throws Exception {
        ByteBuf buffer = null;
        // buf1 等于null .一定选cache
        if (buf1 == null) {
            buffer = buf2;
        } else {
            buffer = buf1;
        }
        while (buffer.isReadable()) {
            int start = buffer.readerIndex();
            try {
                short file_version = buffer.readShort();
                // file_version
                if (file_version != Constants.FILE_PROTOCOL_VERSION) {
                    buffer.readerIndex(start);
                    // 处理普通协议
                    handler(buffer, out);
                    return;
                }

                // file_flag
                short file_flag = buffer.readShort();
                // 如果不等于
                if (file_flag != Constants.FILE_START_VERSION) {
                    if (file_flag == Constants.FILE_END_VERSION) {
                        removeChannel(buffer, ctx);
                        continue;
                    } else {
                        buffer.readerIndex(start);
                        // 处理普通协议
                        handler(buffer, out);
                        return;
                    }
                }
                // 文件名
                short file_name_size = buffer.readShort();
                byte[] bytes = new byte[file_name_size];
                buffer.readBytes(bytes, 0, file_name_size);
                String file_name = new String(bytes);

                // 文件开始位置
                long file_start = buffer.readLong();
                // 写入文件长度
                int file_len = buffer.readInt();
                // 如果长度不足,我们就cache住
                if (file_len > (buffer.writerIndex() - buffer.readerIndex())) {
                    // 重置/ 不管是不是buf
                    buffer.readerIndex(start);
                    // 直接创建一个(防止计数问题)
                    ByteBuf buf = ctx.alloc().directBuffer(buffer.writerIndex() - buffer.readerIndex());
                    // 写入到我们创建的buf中
                    buf.writeBytes(buffer);
                    // buf1 为空 一定是cache.
                    if (buf1 == null) {
                        // 这里是cache.我们需要释放
                        buffer.release(buffer.refCnt());
                    }
                    // 释放这个buffer
                    cache = buf;
                    return;
                } else {
                    FileChannel channel = getChannel(file_name);
                    // 通过dir直接写入
                    buffer.readBytes(channel, file_start, file_len);
                }
            } catch (Exception e) {
                // 如果出现读异常, 就是读不到数据. 直接重置,给下一个处理.最后返回
                buffer.readerIndex(start);
                handler(buffer, out);
                return;
            }
        }
    }

    /**
     * 移除文件流
     */
    private void removeChannel(ByteBuf buffer, ChannelHandlerContext context) {
        short file_name_size = buffer.readShort();
        byte[] bytes = new byte[file_name_size];
        buffer.readBytes(bytes, 0, file_name_size);
        String file_name = new String(bytes);
        Pair<FileOutputStream, FileChannel> pair = file_map.get(file_name);
        if (pair != null) {
            try {
                FileChannel channel = pair.getV();
                if (channel != null) {
                    channel.close();
                }
                FileOutputStream stream = pair.getK();
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // 这里一定会有结果的.
                short need = buffer.readShort();
                // 需要返回值
                if (need == Constants.FILE_NEED_RESPONSE) {
                    int id = buffer.readInt();
                    Map<String, String> map = Collections.singletonMap(UrlConstants.ID_KEY, id + "");
                    String url = URL.encode(new URL(UrlConstants.FILE_PROTOCOL, null, 0, map).toString());
                    NPack pack = new NPack(url, file_name.getBytes());
                    context.writeAndFlush(pack);
                    System.out.println(file_name + " : 关闭成功, ID : " + id);
                }
            }
        }
    }


    /**
     * 获取文件流
     */
    private FileChannel getChannel(String file_name) throws FileNotFoundException {
        // 初始化
        if (file_map == null) {
            file_map = new HashMap<>();
        }
        Pair<FileOutputStream, FileChannel> pair = file_map.get(file_name);
        FileChannel channel = null;
        if (pair == null) {
            FileOutputStream outputStream = new FileOutputStream(file_name);
            channel = outputStream.getChannel();
            Pair<FileOutputStream, FileChannel> newPair = new Pair<>(outputStream, channel);
            file_map.put(file_name, newPair);
            pair = file_map.get(file_name);
        }
        channel = pair.getV();
        return channel;
    }


    /**
     * 释放内存
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved0(ctx);
        if (file_map != null) {
            file_map.forEach((s, pair) -> {
                try {
                    FileChannel channel = pair.getV();
                    if (channel != null) {
                        channel.close();
                    }
                    FileOutputStream stream = pair.getK();
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    // no thing
                }
            });
            file_map.clear();
        }
        if (cache != null && cache.refCnt() > 0) {
            cache.release(cache.refCnt());
        }
    }
}
