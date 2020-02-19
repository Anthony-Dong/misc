package com.chat.test;

import com.chat.core.inter.EchoService;
import com.chat.core.util.ThreadPool;
import com.chat.server.handler.ChatServerContext;
import com.chat.server.handler.DefaultChatServerContext;
import com.chat.server.netty.ChatServer;
import com.chat.server.spi.defaulthandler.RpcMapBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 日志设置系统属性 {user.dir}
 */
public class ServerBoot {

    public static void main(String[] args) throws Exception {
        RpcMapBuilder.addService(EchoService.class, new EchoService() {
            @Override
            public String echo() {
                return "hello world";
            }
            @Override
            public Map<String, Object> echo(Map<String, Object> msg, List<String> list) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    //
                }

                System.out.printf("%s\t%s\n", msg.getClass(), list.getClass());
                msg.put("list", list);
                return msg;
            }
        });

        ChatServerContext context = new DefaultChatServerContext();
        context.setThreadPool(new ThreadPool(200, -1, "work"));
        ChatServer.run(9999, context);
    }
}
