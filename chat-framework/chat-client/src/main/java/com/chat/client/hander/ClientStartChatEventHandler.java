package com.chat.client.hander;

import com.chat.client.netty.SyncChatClient;
import com.chat.core.exception.HandlerException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 客户端启动事件处理器
 * <p>
 * {@link SyncChatClient#start}
 *
 * @date:2019/12/24 19:49
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ClientStartChatEventHandler implements ChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientStartChatEventHandler.class);

    private final ChatClientContext chatClientContext;

    ClientStartChatEventHandler(ChatClientContext chatClientContext) {
        this.chatClientContext = chatClientContext;
    }

    @Override
    public void handler(ChatEvent event) throws HandlerException {
        Object obj = event.event();
        if (obj instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) obj;
            if (null != chatClientContext) {
                chatClientContext.onStart();
            }
            logger.error("[客户端] 启动成功 Host:{} Port:{} Version : {} ContextName :{} .", address.getHostName(), address.getPort(), chatClientContext.getVersion(), chatClientContext.getContextName());
        }
    }
}
