package com.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * TODO
 *
 * @date:2020/2/20 14:44
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class TestByteBuf {


    public static void main(String[] args) throws Exception {
        FileOutputStream stream = new FileOutputStream("C:\\Users\\12986\\Desktop\\test.properties");
        FileChannel channel = stream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocateDirect(1000);
        buffer.put("hello world".getBytes());
        buffer.flip();

        channel.write(buffer);

        channel.close();
        stream.close();


//
//        ByteBuf buf = Unpooled.directBuffer(1024);
//        buf.writeBytes(channel, 0, stream.available());
//        System.out.println(buf);
//        stream.close();
//        FileOutputStream stream2 = new FileOutputStream("D:\\代码库\\分布式聊天框架\\chat-framework\\server2.log");
//        FileChannel channel1 = stream2.getChannel();
//        buf.readBytes(channel1, 0, buf.writerIndex());
//
//        stream2.close();

    }
    @Test
    public void test(){
        ByteBuf buf = Unpooled.buffer(1024);

    }

}
