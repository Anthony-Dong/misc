package com.misc.server.handler;

import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEvent;
import com.misc.core.listener.MiscEventType;
import com.misc.server.netty.MiscServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link MiscServerHandler#handlerRemoved(io.netty.channel.ChannelHandlerContext)} 使用
 * <p>
 * {@link MiscEventType#SERVER_HANDLER_REMOVED} 类型
 *
 * @date:2019/12/24 20:47
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ServerHandlerRemovedMiscEventHandler implements MiscEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandlerRemovedMiscEventHandler.class);

    private final MiscServerContext miscServerContext;

    ServerHandlerRemovedMiscEventHandler(MiscServerContext context) {
        this.miscServerContext = context;
    }

    @Override
    public void handler(MiscEvent event) {
        Object o = event.event();
        if (o instanceof ChannelHandlerContext) {
            ChannelHandlerContext context = (ChannelHandlerContext) o;
            if (this.miscServerContext != null) {
                this.miscServerContext.onRemove(context);
            }
            logger.info("[Misc-Server] Remove client success address: {}.", context.channel().remoteAddress().toString());
        }
    }
}
