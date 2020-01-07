package com.chat.test;

import com.chat.core.exception.ContextException;
import com.chat.server.handler.ChatServerContext;
import com.chat.server.netty.ChatServer;
import io.netty.channel.ChannelHandlerContext;


/**
 * 日志设置系统属性 {usr.dir}
 *
 * @date:2019/12/24 17:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerBoot {

    public static void main(String[] args) throws Exception {

        ChatServerContext context = new ChatServerContext("server-1", (short) 1) {
            @Override
            protected void onRemove(ChannelHandlerContext context) throws ContextException {

            }

            @Override
            protected void onRegister(ChannelHandlerContext context) throws ContextException {
            }
        };

        ChatServer.run(9999, context);
    }
}
