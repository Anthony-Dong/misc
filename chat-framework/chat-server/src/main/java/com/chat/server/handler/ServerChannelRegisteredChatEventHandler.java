package com.chat.server.handler;

import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.model.NPack;
import com.chat.server.netty.ChatServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ChatServerHandler#channelRegistered(io.netty.channel.ChannelHandlerContext)}
 *
 * @date:2019/12/24 20:47
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerChannelRegisteredChatEventHandler implements ChatEventHandler {
    private final Logger logger = LoggerFactory.getLogger(ServerChannelRegisteredChatEventHandler.class);

    @Override
    public void handler(ChatEvent event) {
        logger.info("[服务器] 客户端注册成功.");

        Object obj = event.event();
        if (obj instanceof ChannelHandlerContext) {
            ChannelHandlerContext context = (ChannelHandlerContext) obj;
            context.writeAndFlush(new NPack("[服务器]", "欢迎注册成功"));
        }
    }
}
