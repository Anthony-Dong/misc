package com.chat.core.netty_file;

import com.chat.core.model.NPack;
import com.chat.core.model.URL;
import com.chat.core.model.UrlConstants;
import com.chat.core.netty.Constants;
import com.chat.core.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件服务
 *
 * @date:2020/2/20 16:11
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class FileServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FileServerHandler.class);
    /**
     * 保存文件的channel和stream. 防止大量申请资源
     */
    private Map<String, Pair<FileOutputStream, FileChannel>> file_map;

    /**
     * 一个临时对象,保存未被处理的对象
     */
    private ByteBuf save;


    /**
     * 只负责读取文件,其他委派给别人
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            // 如果没有缓存.直接处理
            if (save == null || save.refCnt() == 0) {
                ByteBuf buffer = (ByteBuf) msg;
                handlerFile(ctx, buffer);
            } else {
                // 有缓存,但是真的最好别用缓存
                ByteBuf buffer = (ByteBuf) msg;
                save.writeBytes(buffer);
                // 释放buf
                buffer.release();
                handlerFile(ctx, save);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 处理流
     *
     * @param ctx    当前上下文
     * @param buffer 当前的buffer对象 (可能是save对象,或者是msg对象)
     * @throws IOException 异常
     */
    private void handlerFile(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        while (buffer.isReadable()) {
            int start = buffer.readerIndex();
            try {
                short file_version = buffer.readShort();
                // file_version
                if (file_version != Constants.FILE_PROTOCOL_VERSION) {
                    buffer.readerIndex(start);
                    // 传给下一个处理器.
//                    try {
//                        ctx.fireChannelRead(buffer);
//                    } finally {
//                        // 必须释放内存
//                        if (buffer != null && buffer.refCnt() > 0) {
//                            buffer.release(buffer.refCnt());
//                        }
//                    }
                    return;
                }

                // file_flag
                short file_flag = buffer.readShort();
                // 如果不等于
                if (file_flag != Constants.FILE_START_VERSION) {
                    if (file_flag == Constants.FILE_END_VERSION) {
                        removeChannel(buffer, ctx);
                        // 万一是下一个文件,就需要继续读取了
                        continue;
                    } else {
                        // 这俩都不符合,说明.不属于我们管理,给别人使用
                        buffer.readerIndex(start);
                        // 传给下一个处理器.
//                        try {
//                            ctx.fireChannelRead(buffer);
//                        } finally {
//                            // 必须释放内存
//                            if (buffer != null && buffer.refCnt() > 0) {
//                                buffer.release(buffer.refCnt());
//                            }
//                        }
                        // 直接return
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
                    // 直接创建一个
                    ByteBuf buf = ctx.alloc().directBuffer(buffer.writerIndex() - buffer.readerIndex());
                    // 写入到我们创建的buf中
                    buf.writeBytes(buffer);
                    // 释放这个buffer
                    buffer.release(buffer.refCnt());
                    save = buf;
                    return;
                } else {
                    FileChannel channel = getChannel(file_name);
                    // 通过dir直接写入
                    buffer.readBytes(channel, file_start, file_len);
                }
            } catch (Exception e) {
                // 如果出现读异常, 就是读不到数据. 直接重置,给下一个处理.最后返回
                buffer.readerIndex(start);
//                try {
//                    ctx.fireChannelRead(buffer);
//                } finally {
//                    // 必须释放内存
//                    if ( buffer.refCnt() > 0) {
//                        buffer.release(buffer.refCnt());
//                    }
//                }
                // 直接return
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
                    String path = Constants.DEFAULT_FILE_DIR + Constants.FILE_SEPARATOR + file_name;
                    NPack pack = new NPack(url, path.getBytes());
                    context.writeAndFlush(pack);
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
            FileOutputStream outputStream = new FileOutputStream(Constants.DEFAULT_FILE_DIR + Constants.FILE_SEPARATOR + file_name);
            channel = outputStream.getChannel();
            Pair<FileOutputStream, FileChannel> newPair = new Pair<>(outputStream, channel);
            file_map.put(file_name, newPair);
            pair = file_map.get(file_name);
        }
        channel = pair.getV();
        return channel;
    }


    /**
     * 释放资源
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
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
        if (save != null && save.refCnt() > 0) {
            save.release(save.refCnt());
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error("[服务器] 文件服务发生异常 客户端 IP : {} 将断开连接 Exception : {}.", ctx.channel().remoteAddress(), cause.getMessage());
    }
}
