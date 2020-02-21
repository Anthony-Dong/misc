package com.chat.core.netty;


import com.chat.core.model.NPack;
import com.chat.core.model.URL;
import com.chat.core.model.UrlConstants;
import com.chat.core.netty_file.PackageDecoder;
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
 * 这里会处理两种协议. 一种是Npack. 一种是文件.
 * <p>
 * 两种交替使用. 对于多余的数据无法处理的. ByteToMessageDecoder会给你保存无法处理的对象.
 *
 * @date:2019/11/10 13:40
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public final class FileAndPackageDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(PackageDecoder.class);
    /**
     * 保存文件的channel和stream. 防止大量申请资源
     */
    private Map<String, Pair<FileOutputStream, FileChannel>> file_map;

    /**
     * 默认值是 {@link com.chat.core.netty.Constants#PROTOCOL_VERSION}
     */
    private final short VERSION;
    private static final MessagePack pack = new MessagePack();

    /**
     * 构造方法
     */
    public FileAndPackageDecoder(short version) {
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
        handlerNpack(in, out);

        // 处理文件包, 可能还需要进行decode
        handlerFile(ctx, in, out);
    }

    /**
     * 处理Napck
     */
    private void handlerNpack(ByteBuf in, List<Object> out) {
        // 如果可读
        while (in.isReadable()) {
            // 防止读取version的时候发生错误
            if (in.readableBytes() < 3) {
                return;
            }

            // 1. 记录最开始读取的位置 , 防止出错
            int start = in.readerIndex();
            // 2. 读取版本号 , 2个字节
            short version = in.readShort();
            if (version != VERSION) {
                //重置
                in.readerIndex(start);
                return;
            }
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
                // 8. 如果么问题, 就进行解码 -> 抓取异常
                read = pack.read(bytes, NPack.class);
            } catch (Exception e) {
                // 抓取异常, 可能是不够读. 也可能是编码错误.
            } finally {
                // 快速释放,防止内存溢出(如果数据很大显式的释放很棒.)
                bytes = null;
            }
            // message-pack 解码错误,重置.返回
            if (read == null) {
                // 重置
                in.readerIndex(start);
                return;
            } else {
                // 添加继续
                out.add(read);
            }
        }

    }

    /**
     * buf1传入的是in(ByteToMessage管的) , buf2传入的是cache.
     * 为了区分开
     * //     * 请看 {@link com.chat.client.context}
     */
    private void handlerFile(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        while (buffer.isReadable()) {
            int start = buffer.readerIndex();
            try {
                short file_version = buffer.readShort();
                // file_version
                if (file_version != Constants.FILE_PROTOCOL_VERSION) {
                    buffer.readerIndex(start);
                    // 处理NPACK协议
                    handler(buffer, out);
                    return;
                }

                // file_flag
                short file_flag = buffer.readShort();
                // 如果不等于
                if (file_flag != Constants.FILE_START_VERSION) {
                    if (file_flag == Constants.FILE_END_VERSION) {
                        removeChannel(buffer, ctx);
                        // 继续处理文件
                        continue;
                    } else {
                        // 重置处理普通协议
                        buffer.readerIndex(start);
                        handlerNpack(buffer, out);
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
                    // 重置, 交给message-byte处理 , 他会帮你攒下来,这里不交给Npack的原因,一定是文件协议
                    buffer.readerIndex(start);
                    return;
                } else {
                    // 获取channel对象, 直接写入
                    FileChannel channel = null;
                    try {
                        channel = getChannel(file_name);
                    } catch (FileNotFoundException e) {
                        // 如果文件创建失败. 无语,直接移除客户端.清空缓冲区.
                        super.handlerRemoved0(ctx);
                    }
                    // 通过NIO直接写入
                    buffer.readBytes(channel, file_start, file_len);
                }
            } catch (Exception e) {
                // 如果出现读异常, 就是读不到数据. 直接重置,给下一个处理.此时应该放弃了
                buffer.readerIndex(start);
                // 处理 Npack
                handlerNpack(buffer, out);
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
                    NPack pack = new NPack(url, getPath(file_name).getBytes());
                    context.writeAndFlush(pack);
                }
            }
        }
    }

    private static String getPath(String fileName) {
        return Constants.DEFAULT_FILE_DIR + Constants.FILE_SEPARATOR + fileName;
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
            FileOutputStream outputStream = null;
            outputStream = new FileOutputStream(getPath(file_name));
            channel = outputStream.getChannel();
            // 创建个新的
            Pair<FileOutputStream, FileChannel> newPair = new Pair<>(outputStream, channel);
            // 放入
            file_map.put(file_name, newPair);
            // 拿出来
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
        try {
            super.handlerRemoved0(ctx);
        } finally {
            // 一定要清空
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
}
