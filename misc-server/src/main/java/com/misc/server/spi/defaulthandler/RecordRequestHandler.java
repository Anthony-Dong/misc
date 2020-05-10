package com.misc.server.spi.defaulthandler;

import com.misc.core.exception.HandlerException;
import com.misc.core.model.UrlConstants;
import com.misc.core.model.netty.Request;
import com.misc.server.spi.handler.AbstractRequestHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 记录日志的
 *
 * @date:2020/2/17 20:27
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RecordRequestHandler extends AbstractRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RecordRequestHandler.class);

    @Override
    public void handler(Request request, ChannelHandlerContext context) throws HandlerException {
        try {
            fireHandler(request, context);
        } finally {
            if (!request.getProtocol().equals(UrlConstants.HEART_PROTOCOL)) {
                logger.debug("[Misc-Server] HandlerRequest protocol: {}, url: {}, spend: {}ms.", request.getProtocol(), request.getUrl(), System.currentTimeMillis() - request.getTimestamp());
            }
        }
    }
}
