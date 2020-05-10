package com.misc.server.spi.defaulthandler;

import com.misc.core.exception.HandlerException;
import com.misc.core.model.UrlConstants;
import com.misc.core.model.netty.Request;
import com.misc.server.spi.handler.AbstractRequestHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @date:2020/2/17 14:42
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class FileRequestHandler extends AbstractRequestHandler {

    @Override
    public void handler(Request request, ChannelHandlerContext context) throws HandlerException {
        if (request.getProtocol().equals(UrlConstants.FILE_PROTOCOL)) {
            // 处理文件协议
        } else {
            fireHandler(request, context);
        }
    }
}
