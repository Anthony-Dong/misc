package com.misc.client.hander;

import com.misc.core.exception.HandlerException;
import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEvent;
import com.misc.core.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 客户端处理器
 *
 * @date:2019/12/24 19:50
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientShutDownMiscEventHandler implements MiscEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientShutDownMiscEventHandler.class);

    private final MiscClientContext miscClientContext;

    ClientShutDownMiscEventHandler(MiscClientContext miscClientContext) {
        this.miscClientContext = miscClientContext;
    }

    @Override
    public void handler(MiscEvent event) throws HandlerException {
        Object obj = event.event();
        if (obj instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) obj;
            if (null != miscClientContext) {
                logger.debug("[客户端] Disconnect server host: {}, port: {}, version:{}, type: {}, contextName:{}, thread-size: {}, thread-queue-size: {}, thread-name: {}.", NetUtils.filterLocalHost(address.getHostName()), address.getPort()
                        , miscClientContext.getVersion(), miscClientContext.getMiscSerializableType(), miscClientContext.getContextName()
                        , miscClientContext.getThreadPool().getPoolSize(), miscClientContext.getThreadPool().getQueueSize(), miscClientContext.getThreadPool().getThreadGroupName()
                );
            }
        }
    }
}
