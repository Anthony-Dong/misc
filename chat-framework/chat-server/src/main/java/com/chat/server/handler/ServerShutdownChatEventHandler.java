package com.chat.server.handler;

import com.chat.core.exception.HandlerException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 *  {@link ChatEventType#SERVER_SHUTDOWN}
 *
 * @date:2019/12/24 20:42
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerShutdownChatEventHandler implements ChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServerShutdownChatEventHandler.class);

    private final ChatServerContext chatServerContext;

    ServerShutdownChatEventHandler(ChatServerContext context) {
        this.chatServerContext = context;
    }

    @Override
    public void handler(ChatEvent event) throws HandlerException {
        Object obj = event.event();
        if (obj instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) obj;
            if (this.chatServerContext != null) {
                this.chatServerContext.onFail(address);
            }
            logger.info("[服务器] 关闭成功 Host:{} , Port:{} , Version:{} , ContextName:{}.", address.getHostName(), address.getPort(), chatServerContext.getVersion(), chatServerContext.getContextName());
        }
    }
}
