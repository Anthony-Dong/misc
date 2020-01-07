package com.chat.test;

import com.chat.client.hander.ChatClientContext;
import com.chat.client.netty.ChatClient;
import com.chat.core.model.Message;
import com.chat.core.model.NPack;
import com.chat.core.util.FileUtil;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.List;


/**
 * @date:2019/12/24 17:22
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientBoot2 {

    public static void main(String[] args) throws Exception {
        final ChatClientContext context = new ChatClientContext("app-1", (short) 1) {
            @Override
            protected void onStart() {
                //启动
            }

            @Override
            protected void onFail() {
                // 关闭
            }

            @Override
            protected void onReading(NPack context) {
                // 读
            }
        };
        ChatClient.run(9999, context);
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
