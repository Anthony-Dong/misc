package com.chat.server.handler;

import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventType;
import com.chat.server.netty.ChatServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ChatServerHandler#handlerRemoved(io.netty.channel.ChannelHandlerContext)} 使用
 * <p>
 * {@link ChatEventType#SERVER_HANDLER_REMOVED} 类型
 *
 * @date:2019/12/24 20:47
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ServerHandlerRemovedChatEventHandler implements ChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandlerRemovedChatEventHandler.class);

    private final ChatServerContext chatServerContext;

    ServerHandlerRemovedChatEventHandler(ChatServerContext context) {
        this.chatServerContext = context;
    }

    @Override
    public void handler(ChatEvent event) {
        Object o = event.event();
        if (o instanceof ChannelHandlerContext) {
            ChannelHandlerContext context = (ChannelHandlerContext) o;
            if (this.chatServerContext != null) {
                this.chatServerContext.onRemove(context);
            }
            logger.info("[服务器] Remove client address: {}.", context.channel().remoteAddress().toString());
        }
    }
}
