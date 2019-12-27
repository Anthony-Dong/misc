package com.chat.test;

import com.chat.core.exception.ContextException;
import com.chat.server.context.DefaultChatServerContext;
import com.chat.server.context.HandlerOtherOperation;
import com.chat.server.handler.ChatServerContext;
import com.chat.server.netty.ChatServer;
import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.Jedis;

/**
 * @date:2019/12/24 17:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerBoot {

    public static void main(String[] args) {
        DefaultChatServerContext context = new DefaultChatServerContext("server-1", new HandlerOtherOperation() {
            @Override
            public void onRemove(ChannelHandlerContext context, Jedis jedis) throws ContextException {
                System.out.println("=====onRemove======");
            }

            @Override
            public void onRegister(ChannelHandlerContext context, Jedis jedis) throws ContextException {
                System.out.println("======onRegister=====");
            }
        });

        try {
            ChatServer.run(8888, context);
        } catch (Exception e) {
            //
        }
    }
}
