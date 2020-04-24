package com.chat.client.hander;

import com.chat.core.exception.HandlerException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 客户端处理器
 *
 * @date:2019/12/24 19:50
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientShutDownChatEventHandler implements ChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientShutDownChatEventHandler.class);

    private final ChatClientContext chatClientContext;

    ClientShutDownChatEventHandler(ChatClientContext chatClientContext) {
        this.chatClientContext = chatClientContext;
    }

    @Override
    public void handler(ChatEvent event) throws HandlerException {
        Object obj = event.event();
        if (obj instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) obj;
            if (null != chatClientContext) {
                logger.debug("[客户端] Disconnect server host: {}, port: {}, version:{}, type: {}, contextName:{}, thread-size: {}, thread-queue-size: {}, thread-name: {}.", NetUtils.filterLocalHost(address.getHostName()), address.getPort()
                        , chatClientContext.getVersion(), chatClientContext.getSerializableType(), chatClientContext.getContextName()
                        , chatClientContext.getThreadPool().getPoolSize(), chatClientContext.getThreadPool().getQueueSize(), chatClientContext.getThreadPool().getThreadGroupName()
                );
                chatClientContext.onShutdown();
            }
        }
    }
}
