package com.chat.test;

import com.chat.client.hander.ChatClientContext;
import com.chat.client.netty.AsyncChatClient;
import com.chat.core.model.Message;
import com.chat.core.model.NPack;
import com.chat.core.util.FileUtil;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


/**
 * @date:2019/12/24 17:22
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ASyncClientBoot {

    public static void main(String[] args) throws Exception {
        ChatClientContext context = new ChatClientContext() {};
        AsyncChatClient client = AsyncChatClient.run(9999, context);
        context.sendPack(NPack.buildWithJsonBody("b", "a", Collections.singletonList("A")));
        client.close();
    }

    private static void task() throws Exception {
        ChatClientContext context = ChatClientContext.newInstance();
        AsyncChatClient client = AsyncChatClient.run(9999, context);
        context.sendPack(NPack.buildWithJsonBody("b", "a", Collections.singletonList("hello world")));
        client.close();
    }

    private static void testJson(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(NPack.buildWithJsonBody("b", "a", new Message("info", "name", System.currentTimeMillis())));
    }

    private static void testString(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(NPack.buildWithStringBody("a", "c", "a.txt"));
    }

    private static void testFileUpload(ChannelHandlerContext channelHandlerContext) throws Exception {

        File file = new File("C:\\Users\\12986\\Desktop\\作业.docx");

        List<byte[]> bytes = FileUtil.cuttingFile(file, FileUtil.LEN_10_KB);

        bytes.forEach(e -> channelHandlerContext.writeAndFlush(NPack.buildWithByteBody("a", "b", "作业.docx", e)));
    }

    private static void testError(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(new NPack("aaaa"));
    }
}
