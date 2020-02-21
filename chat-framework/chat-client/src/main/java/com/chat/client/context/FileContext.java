package com.chat.client.context;

import com.chat.core.exception.ContextException;
import com.chat.core.model.netty.Response;
import com.chat.core.netty.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @date:2020/2/20 21:41
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface FileContext {

    void sendFile(File file, String fileName, int splitSize) throws ContextException;


    String sendFileSync(File file, String fileName, int splitSize) throws ContextException;


    /**
     * 文件传输协议
     * 版本：version（2）   20000-29999 -> 文件协议
     * 文件校验 : flag(2)   30000-31000 ->文件开始标识符 , 文件结尾标识符
     * name-len : flag(2)  文件名 占两位
     * 写入file_name
     * file_write_index    文件写入位置8个长度
     * file_len :  // 文件分割长度占4个字节
     * 写入file :  // 文件长度
     *
     * @param context  上下文
     * @param file     文件
     * @param fileName 告诉要保存时候的文件名
     * @throws IOException 异常
     */
    static void sendFileMethod(ChannelHandlerContext context, File file, String fileName, boolean isACK, int id, int file_byte_len) throws IOException {
        // 文件版本号
        int file_version_len = 2;
        // 标识符,进一步确认是文件协议
        int file_flag_len = 2;
        // 文件名长度占用两个字节
        int file_name_len = 2;
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
                buf.writeShort(Constants.FILE_PROTOCOL_VERSION);
                buf.writeShort(Constants.FILE_START_VERSION);
                buf.writeShort(file_name_byte_len);
                buf.writeBytes(file_name_byte);
                buf.writeLong(start);
                buf.writeInt(file_byte_len);
                buf.writeBytes(channel, start, file_byte_len);
                context.writeAndFlush(buf);
                start += file_byte_len;
            }
            // 剩余需要写的长度
            int tail = (int) (capacity - start);
            // 需要创建的内存
            int tail_size = file_version_len + file_flag_len + file_name_len + file_name_byte_len + file_start_len + file_len + tail;
            ByteBuf end = Unpooled.directBuffer(tail_size);
            end.writeShort(Constants.FILE_PROTOCOL_VERSION);
            end.writeShort(Constants.FILE_START_VERSION);
            end.writeShort(file_name_byte_len);
            end.writeBytes(file_name_byte);
            end.writeLong(start);
            // 长度
            end.writeInt(tail);
            // 写入文件内容
            end.writeBytes(channel, start, tail);
            context.writeAndFlush(end);
        } finally {
            ByteBuf shutdown = Unpooled.directBuffer();
            shutdown.writeShort(Constants.FILE_PROTOCOL_VERSION);
            shutdown.writeShort(Constants.FILE_END_VERSION);
            shutdown.writeShort(file_name_byte_len);
            shutdown.writeBytes(file_name_byte);
            // 是否需要同步?
            if (isACK) {
                shutdown.writeShort(Constants.FILE_NEED_RESPONSE);
                shutdown.writeInt(id);
            } else {
                shutdown.writeShort(Constants.FILE_NULL_RESPONSE);
            }
            context.writeAndFlush(shutdown);
        }
    }
}
