package com.chat.server.handler;

import com.chat.core.exception.HandlerException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventType;
import com.chat.core.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;

/**
 * 服务端端启动事件处理器
 * <p>
 * {@link ChatEventType#SERVER_START}
 *
 * @date:2019/12/24 19:49
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerStartChatEventHandler implements ChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServerStartChatEventHandler.class);

    private final ChatServerContext chatServerContext;

    ServerStartChatEventHandler(ChatServerContext context) {
        this.chatServerContext = context;
    }

    @Override
    public void handler(ChatEvent event) throws HandlerException {
        Object obj = event.event();
        if (obj instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) obj;
            if (this.chatServerContext != null) {
                this.chatServerContext.onStart(address);
            }
            logger.debug("[服务器] Start-up success host: {}, port: {}, version:{}, type: {}, contextName:{}, thread-size: {}, thread-queue-size: {}, thread-name: {}.", NetUtils.filterLocalHost(address.getHostName()), address.getPort()
                    , chatServerContext.getVersion(), chatServerContext.getSerializableType(), chatServerContext.getContextName()
                    , chatServerContext.getThreadPool().getPoolSize(), chatServerContext.getThreadPool().getQueueSize(), chatServerContext.getThreadPool().getThreadGroupName()
            );
        }
    }
}
