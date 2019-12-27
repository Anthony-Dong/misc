package com.chat.test;

import com.chat.client.hander.ChatClientContext;
import com.chat.client.netty.ChatClient;
import com.chat.core.model.Message;
import com.chat.core.model.NPack;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * @date:2019/12/24 17:22
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientBoot {

    public static void main(String[] args) throws Exception {

        final ChatClientContext context = new ChatClientContext("app-1") {
            @Override
            protected void onStart() {
                System.out.println("onStart");
            }

            @Override
            protected void onFail() {
                System.out.println("onFail");
            }

            @Override
            protected void onReading(NPack context) {
                System.out.println("onReading" + context);
            }

        };


        new Thread(() -> {
            try {
                ChatClient.run(8888, context);
            } catch (Exception ignored) {

            }
        }).start();


        ChannelHandlerContext channelHandlerContext = context.getContext();


        // 测试异常
        testError(channelHandlerContext);

        testFileUpload(channelHandlerContext);

        testString(channelHandlerContext);

        testJson(channelHandlerContext);


    }

    private static void testJson(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(NPack.buildWithJsonBody("b", "a", new Message("info", "name", System.currentTimeMillis())));
    }

    private static void testString(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(NPack.buildWithStringBody("a", "b", "a.txt"));
    }

    private static void testFileUpload(ChannelHandlerContext channelHandlerContext) throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File("C:\\Users\\12986\\Desktop\\file.txt"), "r");
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        file.read(bytes);
        channelHandlerContext.writeAndFlush(NPack.buildWithByteBody("a", "b", "file3.txt", bytes));
        file.close();
    }

    private static void testError(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(new NPack("aaaa"));
    }
}
