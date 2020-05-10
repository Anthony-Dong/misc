package com.misc.server.handler;

import com.misc.core.exception.HandlerException;
import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEvent;
import com.misc.server.netty.MiscServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link MiscServerHandler#channelRegistered(io.netty.channel.ChannelHandlerContext)}
 *
 * @date:2019/12/24 20:47
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerChannelRegisteredMiscEventHandler implements MiscEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServerChannelRegisteredMiscEventHandler.class);


    private final MiscServerContext miscServerContext;

    ServerChannelRegisteredMiscEventHandler(MiscServerContext context) {
        this.miscServerContext = context;
    }

    @Override
    public void handler(MiscEvent event) throws HandlerException {
        Object obj = event.event();
        if (obj instanceof ChannelHandlerContext) {
            ChannelHandlerContext context = (ChannelHandlerContext) obj;
            if (this.miscServerContext != null) {
                this.miscServerContext.onRegister(context);
            }
            logger.debug("[Misc-Server] Registered client address: {}.", context.channel().remoteAddress().toString());
        }
    }
}
