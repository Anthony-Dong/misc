package com.chat.client.hander;

import com.chat.client.netty.ChantClientHandler;
import com.chat.core.exception.HandlerException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端连接服务器端成功
 * <p>
 * {@link ChantClientHandler#channelActive} 处理器
 * <p>
 * 当连接真正建立成功 我们把上下文拿出来
 *
 * @date:2019/12/24 19:46
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientConnectedChatEventHandler implements ChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientReadChatEventHandler.class);
    private final ChatClientContext chatClientContext;


    ClientConnectedChatEventHandler(ChatClientContext chatClientContext) {
        this.chatClientContext = chatClientContext;
    }

    /**
     * @param event ChannelHandlerContext
     */
    @Override
    public void handler(ChatEvent event) throws HandlerException {
        Object object = event.event();
        if (object instanceof ChannelHandlerContext) {
            ChannelHandlerContext context = (ChannelHandlerContext) object;
            // 将拿到的上下文对象取出来
            chatClientContext.setContext(context);
            // 启动,代表ChannelHandlerContext已经拿到了.可以运行了
            chatClientContext.getLatch().countDown();
            logger.debug("[客户端] 注册成功 ServerAddress : {}.", context.channel().remoteAddress());
        }
    }
}