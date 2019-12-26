package com.chat.client.hander;

import com.chat.client.netty.ChantClientHandler;
import com.chat.client.spi.HandlerSenderPackage;
import com.chat.core.exception.HandlerException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.spi.SPIUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端连接服务器端成功
 * <p>
 * {@link ChantClientHandler#channelActive(io.netty.channel.ChannelHandlerContext)} 处理器
 * <p>
 * 当连接真正建立成功 我们把上下文拿出来
 *
 * @date:2019/12/24 19:46
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientConnectedChatEventHandler implements ChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientReadChatEventHandler.class);
    private final HandlerSenderPackage handler;
    private final ChatClientContext chatClientContext;


    ClientConnectedChatEventHandler(ChatClientContext chatClientContext) {
        this.handler = SPIUtil.loadClass(HandlerSenderPackage.class, ClassLoader.getSystemClassLoader());
        this.chatClientContext = chatClientContext;
    }


    /**
     * 拓展接口
     *
     * @param event ChannelHandlerContext
     */
    @Override
    public void handler(ChatEvent event) throws HandlerException {
        Object object = event.event();
        if (object instanceof ChannelHandlerContext) {
            ChannelHandlerContext context = (ChannelHandlerContext) object;
            // 交给SPI处理器
            chatClientContext.setContext(context);
            handler.senderPack(context);
            logger.info("[客户端] 注册成功 , IP : {}.", context.channel().remoteAddress());
        }
    }

}