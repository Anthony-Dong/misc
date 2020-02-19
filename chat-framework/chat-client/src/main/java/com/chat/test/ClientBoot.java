package com.chat.test;

import com.chat.client.hander.ChatClientContext;
import com.chat.client.netty.SyncChatClient;
import com.chat.core.model.NPack;
import com.chat.core.model.NpackBuilder;
import com.chat.core.util.FileUtil;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.List;


/**
 * @date:2019/12/24 17:22
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientBoot {

    public static void main(String[] args) throws Exception {
        final ChatClientContext context = new ChatClientContext() {

            @Override
            public void onBootstrap() {

            }

            @Override
            public void onShutdown() {

            }
        };
        SyncChatClient.run(9999, context);
    }

    private static void testJson(ChannelHandlerContext channelHandlerContext) {
        //channelHandlerContext.writeAndFlush(NpackBuilder.buildWithJsonBody("b", "a", new Message("info", "name", System.currentTimeMillis())));
    }

    private static void testString(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(NpackBuilder.buildWithStringBody("a", "c", "a.txt"));
    }

    private static void testFileUpload(ChannelHandlerContext channelHandlerContext) throws Exception {

        File file = new File("C:\\Users\\12986\\Desktop\\作业.docx");

        List<byte[]> bytes = FileUtil.cuttingFile(file, FileUtil.LEN_10_KB);

        bytes.forEach(e -> channelHandlerContext.writeAndFlush(NpackBuilder.buildWithByteBody("a", "b", "作业.docx", e)));
    }

    private static void testError(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(new NPack("aaaa"));
    }
}
