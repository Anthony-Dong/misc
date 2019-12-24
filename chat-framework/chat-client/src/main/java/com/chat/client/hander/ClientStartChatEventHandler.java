package com.chat.client.hander;

import com.chat.client.netty.ChatClient;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 客户端启动事件处理器
 * <p>
 * {@link ChatClient#start}
 *
 * @date:2019/12/24 19:49
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ClientStartChatEventHandler implements ChatEventHandler {
    private final Logger logger = LoggerFactory.getLogger(ClientStartChatEventHandler.class);

    @Override
    public void handler(ChatEvent event) {
        Object obj = event.event();
        if (obj instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) obj;
            logger.error("[客户端] 启动成功 Host:{} Port:{}.", address.getHostName(), address.getPort());
        }
    }
}
