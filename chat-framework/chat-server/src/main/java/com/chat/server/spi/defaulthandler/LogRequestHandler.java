package com.chat.server.spi.defaulthandler;

import com.chat.core.exception.HandlerException;
import com.chat.core.model.UrlConstants;
import com.chat.core.model.netty.Request;
import com.chat.server.spi.handler.AbstractRequestHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * TODO
 *
 * @date:2020/2/18 10:09
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class LogRequestHandler extends AbstractRequestHandler {

    /**
     * 需要实现的接口
     */
    @Override
    public void handler(Request request, ChannelHandlerContext context) throws HandlerException {
        if (request.getProtocol().equals(UrlConstants.LOG_PROTOCOL)) {
            System.out.println(new String(request.getBody()));
        } else {
            fireHandler(request, context);
        }
    }
}
