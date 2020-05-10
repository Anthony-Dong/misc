package com.misc.client.hander;

import com.misc.client.netty.ChantClientHandler;
import com.misc.core.exception.HandlerException;
import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEvent;
import com.misc.core.util.NetUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端连接服务器端成功
 * <p>
 * {@link ChantClientHandler#channelActive} 处理器
 * <p>
 * 当连接真正建立成功 我们把上下文拿出来
 *
 * @date:2019/12/24 19:46
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientConnectedMiscEventHandler implements MiscEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientReadMiscEventHandler.class);
    private final MiscClientContext miscClientContext;


    ClientConnectedMiscEventHandler(MiscClientContext miscClientContext) {
        this.miscClientContext = miscClientContext;
    }

    /**
     * @param event ChannelHandlerContext
     */
    @Override
    public void handler(MiscEvent event) throws HandlerException {
        Object object = event.event();
        if (object instanceof ChannelHandlerContext) {
            ChannelHandlerContext context = (ChannelHandlerContext) object;
            // 将拿到的上下文对象取出来
            miscClientContext.setContext(context);
            // 启动,代表ChannelHandlerContext已经拿到了.可以运行了
            miscClientContext.getLatch().countDown();
            logger.debug("[客户端] Connect server success host: {}, port: {}, version:{}, type: {}, contextName:{}, thread-size: {}, thread-queue-size: {}, thread-name: {}.", NetUtils.filterLocalHost(miscClientContext.getHost()), miscClientContext.getPort()
                    , miscClientContext.getVersion(), miscClientContext.getSerializableType(), miscClientContext.getContextName()
                    , miscClientContext.getThreadPool().getPoolSize(), miscClientContext.getThreadPool().getQueueSize(), miscClientContext.getThreadPool().getThreadGroupName()
            );
        }
    }
}