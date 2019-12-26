package com.chat.server.handler;

import com.chat.core.exception.HandlerException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventType;
import com.chat.core.model.NPack;
import com.chat.core.netty.Constants;
import com.chat.server.netty.ChatServerHandler;
import com.chat.server.spi.HandlerReceivePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ChatServerHandler#channelRead0(io.netty.channel.ChannelHandlerContext, com.chat.core.model.NPack)} 使用
 * <p>
 * {@link ChatEventType#SERVER_READ} 类型
 *
 * @date:2019/12/24 20:44
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ServerReadChatEventHandler implements ChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServerReadChatEventHandler.class);

    private final HandlerReceivePackage handler;

    ServerReadChatEventHandler() {
        this.handler = new HandlerReceivePackage();
    }

    @Override
    public void handler(ChatEvent event) throws HandlerException {
        Object event1 = event.event();
        if (event1 instanceof NPack) {
            NPack pack = (NPack) event1;
            if (pack.getRouter().equals(Constants.HEART_BEAT_NPACK_ROUTER)) {
                logger.info("[服务器] 心跳信息 : {}.", pack);
            } else {
                handler.handlerNPack(pack);
            }
        }
    }
}
