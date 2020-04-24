package com.chat.core.netty;

import com.chat.core.model.NPack;
import com.chat.core.model.URL;
import com.chat.core.model.UrlConstants;
import com.chat.core.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.chat.core.netty.CodecType.*;

/**
 * 文件协议
 *
 * @date:2020/2/24 18:26
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class FileProtocol {

    /**
     * 保存点
     */
    private Map<String, Pair<FileOutputStream, FileChannel>> file_map;

    /**
     * 保存地址
     */
    private final String save_path;


    private static final int FILE_HEADER_LEN = 13;

    private static final int FILE_DOWN_LEN = 6;

    FileProtocol(String save_path) {
        this.save_path = save_path;
        File file = new File(save_path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 处理文件
     * <p>
     * true : 表示读完
     * false : 需要继续读
     */
    boolean decodeFile(ByteBuf buffer) throws Exception {
        // 如果不足header长度的话. 直接返回
        if (buffer.readableBytes() < FILE_HEADER_LEN) {
            return false;
        }

        // 文件名长度
        byte file_name_len = buffer.readByte();
        // 写入位置
        long file_w_start = buffer.readLong();
        // 写入长度
        int file_w_len = buffer.readInt();

        // 小于文件名长度+文件长度 就 返回需要重置.
        if (buffer.readableBytes() < (file_name_len + file_w_len)) {
            return false;
        }

        byte[] bytes = new byte[file_name_len];

        buffer.readBytes(bytes, 0, file_name_len);
        String file_name = new String(bytes);

        FileChannel channel = getChannel(file_name);
        // 通过NIO直接写入
        buffer.readBytes(channel, file_w_start, file_w_len);
        return true;
    }

    /**
     * 移除文件流
     * true ok
     * false null
     */
    boolean removeChannel(ByteBuf buffer, ChannelHandlerContext context) throws Exception {
        // 文件名长度+ack长度+ack-id=6
        if (buffer.readableBytes() < FILE_DOWN_LEN) {
            return false;
        }

        byte file_name_size = buffer.readByte();
        byte is_ack = buffer.readByte();

        int ack_id = 0;
        if (is_ack == FILE_NEED_ACK) {
            ack_id = buffer.readInt();
        } else {
            buffer.readInt();
        }
        // 文件长度
        if (buffer.readableBytes() < file_name_size) {
            return false;
        }
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
            } finally {
                if (is_ack == FILE_NEED_ACK) {
                    Map<String, String> map = Collections.singletonMap(UrlConstants.ID_KEY, ack_id + "");
                    String url = URL.encode(new URL(UrlConstants.FILE_PROTOCOL, null, 0, map).toString());
                    NPack pack = new NPack(url, getPath(file_name).getBytes());
                    context.writeAndFlush(pack);
                }
            }
        }
        return true;
    }

    /**
     * 文件名
     */
    private String getPath(String fileName) {
        return save_path + Constants.FILE_SEPARATOR + fileName;
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
            FileOutputStream outputStream = new FileOutputStream(getPath(file_name));
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
     */
    void release() {
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


    /**
     * 文件传输协议
     * 魔数 : 一个字节
     * 服务版本号：  version  两个字节
     * 文件协议类型 :  start - end  标志何时开始何时结束 , 1个字节
     * 文件名长度    1个字节
     * 文件写入位置  8个字节
     * 文件写入长度  4个字节
     * 文件名 : ...
     * 文件内容: ...
     *
     * @param context       netty对象
     * @param version       服务版本号
     * @param file          文件名
     * @param fileName      文件名
     * @param isACK         是否支持ACK
     * @param id            ACK-ID 唯一值
     * @param file_byte_len 文件长度
     * @throws IOException 异常
     */
    public static void sendFileMethod(ChannelHandlerContext context, short version, File file, String fileName,
                                      boolean isACK, int id, int file_byte_len) throws IOException {
        // 文件版本号
        int file_version_len = 2;
        // 标识符,进一步确认是文件协议
        int file_flag_len = 1;
        // 文件名长度占用两个字节
        int file_name_len = 1;
        // 文件名
        byte[] file_name_byte = fileName.getBytes();
        int file_name_byte_len = file_name_byte.length;
        // 文件起始位置,占用4个字节
        int file_start_len = 8;
        // 文件长度占用两个字节
        int file_len = 2;
        // 文件长度
//        int file_byte_len = 1024 * 100;
        //  数据长度
        int size = file_version_len + file_flag_len + file_name_len + file_name_byte_len + file_start_len + file_len + file_byte_len;

        try (FileInputStream stream = new FileInputStream(file); FileChannel channel = stream.getChannel()) {
            long start = channel.position();
            long capacity = channel.size();
            while ((capacity - start) > file_byte_len) {
                // 开始写
                ByteBuf buf = Unpooled.directBuffer(size);
                buf.writeByte(MAGIC_NUMBER);
                // 2+1+1+8+4=16
                buf.writeShort(version);
                // 文件开始协议
                buf.writeByte(FILE_START);
                // 文件名长度
                buf.writeByte(file_name_byte_len);
                // 文件起始位置
                buf.writeLong(start);
                // 文件长度
                buf.writeInt(file_byte_len);

                // 文件名
                buf.writeBytes(file_name_byte);
                // 文件数据
                buf.writeBytes(channel, start, file_byte_len);
                context.writeAndFlush(buf);
                start += file_byte_len;
            }
            // 剩余需要写的长度
            int tail = (int) (capacity - start);
            // 需要创建的内存
            int tail_size = file_version_len + file_flag_len + file_name_len + file_name_byte_len + file_start_len + file_len + tail;
            ByteBuf end = Unpooled.directBuffer(tail_size);
            end.writeByte(MAGIC_NUMBER);
            end.writeShort(version);
            end.writeByte(FILE_START);
            end.writeByte(file_name_byte_len);
            end.writeLong(start);
            end.writeInt(tail);
            end.writeBytes(file_name_byte);
            end.writeBytes(channel, start, tail);
            context.writeAndFlush(end);
        } finally {
            ByteBuf shutdown = Unpooled.directBuffer();
            shutdown.writeByte(MAGIC_NUMBER);
            shutdown.writeShort(version);
            shutdown.writeByte(FILE_END);
            // 文件名长度
            shutdown.writeByte(file_name_byte_len);
            if (isACK) {
                shutdown.writeByte(FILE_NEED_ACK);
                shutdown.writeInt(id);
            } else {
                shutdown.writeByte(FILE_NOT_ACK);
                shutdown.writeInt(0);
            }
            // 文件名
            shutdown.writeBytes(file_name_byte);
            context.writeAndFlush(shutdown);
        }
    }
}
