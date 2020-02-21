package com.chat.test;

import com.chat.core.test.EchoService;
import com.chat.core.util.ThreadPool;
import com.chat.server.handler.ChatServerContext;
import com.chat.server.handler.DefaultChatServerContext;
import com.chat.server.netty.ChatServer;
import com.chat.server.spi.defaulthandler.RpcMapBuilder;


/**
 * 日志设置系统属性 {user.dir}
 */
public class ServerBoot {

    public static void main(String[] args) throws Exception {
        RpcMapBuilder.addService(EchoService.class, new EchoService() {
            @Override
            public String hash(int hash) {
                return "hello world";
            }
        });


        ChatServerContext context = new DefaultChatServerContext();
        context.setThreadPool(new ThreadPool(20, -1, "work"));
        context.setUseFileProtocol(true);

        ChatServer.run(9999, context);
    }
}
