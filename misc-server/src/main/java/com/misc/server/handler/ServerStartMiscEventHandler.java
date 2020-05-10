package com.misc.server.handler;

import com.misc.core.exception.HandlerException;
import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEvent;
import com.misc.core.listener.MiscEventType;
import com.misc.core.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 服务端端启动事件处理器
 * <p>
 * {@link MiscEventType#SERVER_START}
 *
 * @date:2019/12/24 19:49
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerStartMiscEventHandler implements MiscEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServerStartMiscEventHandler.class);

    private final MiscServerContext miscServerContext;

    ServerStartMiscEventHandler(MiscServerContext context) {
        this.miscServerContext = context;
    }

    @Override
    public void handler(MiscEvent event) throws HandlerException {
        Object obj = event.event();
        if (obj instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) obj;
            if (this.miscServerContext != null) {
                this.miscServerContext.onStart(address);
            }
            logger.debug("[Misc-Server] Start-up success host: {}, port: {}, version:{}, type: {}, contextName:{}, thread-size: {}, thread-queue-size: {}, thread-name: {}.", NetUtils.filterLocalHost(address.getHostName()), address.getPort()
                    , miscServerContext.getVersion(), miscServerContext.getSerializableType(), miscServerContext.getContextName()
                    , miscServerContext.getThreadPool().getPoolSize(), miscServerContext.getThreadPool().getQueueSize(), miscServerContext.getThreadPool().getThreadGroupName()
            );
        }
    }
}
