package com.chat.test;

import com.chat.core.model.NPack;
import com.chat.server.handler.ChatServerContext;
import com.chat.server.netty.ChatServer;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * @date:2019/12/24 17:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerBoot {

    public static void main(String[] args) {
        ChatServerContext context = new ChatServerContext() {
            @Override
            public void onStart() {
                System.out.println("start");
            }

            @Override
            public void onFail() {
                System.out.println("onFail");
            }

            @Override
            public void onRemove(ChannelHandlerContext context) {
                System.out.println("onRemove");
            }


            @Override
            public void onRegister(ChannelHandlerContext context) {
                context.writeAndFlush(new NPack("注册成功"));
                System.out.println("onRegister");
            }
        };

        try {
            ChatServer.run(8888, context);
        } catch (Exception e) {
            //
        }
    }
}
