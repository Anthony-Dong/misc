package com.misc.server.spi.defaulthandler;

import com.misc.core.exception.HandlerException;
import com.misc.core.model.netty.Request;
import com.misc.server.spi.handler.AbstractRequestHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *心跳处理器
 * @date:2020/2/17 20:40
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class HeartRequestHandler extends AbstractRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(HeartRequestHandler.class);
    /**
     * 处理心跳
     */
    @Override
    public void handler(Request request, ChannelHandlerContext context) throws HandlerException {
        logger.debug("[服务器] Receive Heart: {}, timestamp: {}.", request.getUrl(),request.getTimestamp());
        fireHandler(request, context);
    }
}
