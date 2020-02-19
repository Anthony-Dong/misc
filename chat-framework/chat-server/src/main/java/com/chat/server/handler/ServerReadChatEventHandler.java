package com.chat.server.handler;

import com.chat.core.exception.HandlerException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventType;
import com.chat.core.model.NPack;
import com.chat.server.netty.ChatServerHandler;
import com.chat.server.spi.HandlerReceivePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.chat.core.model.UrlConstants.HEART_PROTOCOL;

/**
 * {@link ChatServerHandler} 使用
 * <p>
 * {@link ChatEventType#SERVER_READ} 类型
 *
 * @date:2019/12/24 20:44
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ServerReadChatEventHandler implements ChatEventHandler {
    private final HandlerReceivePackage handler;

    ServerReadChatEventHandler(ChatServerContext context) {
        this.handler = new HandlerReceivePackage(context);
    }

    @Override
    public void handler(ChatEvent event) throws HandlerException {
        Object event1 = event.event();
        NPack pack = (NPack) event1;
        handler.handlerNPack(pack);
    }
}
