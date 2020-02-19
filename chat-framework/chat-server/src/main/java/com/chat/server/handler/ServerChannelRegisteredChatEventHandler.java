package com.chat.server.handler;

import com.chat.core.exception.HandlerException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
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
    private static final Logger logger = LoggerFactory.getLogger(ServerChannelRegisteredChatEventHandler.class);


    private final ChatServerContext chatServerContext;

    ServerChannelRegisteredChatEventHandler(ChatServerContext context) {
        this.chatServerContext = context;
    }

    @Override
    public void handler(ChatEvent event) throws HandlerException {
        Object obj = event.event();
        if (obj instanceof ChannelHandlerContext) {
            ChannelHandlerContext context = (ChannelHandlerContext) obj;
            if (this.chatServerContext != null) {
                this.chatServerContext.onRegister(context);
            }
            logger.debug("[服务器] Registered Client : {} .", context.channel().remoteAddress().toString());
        }
    }
}
