package com.chat.server.spi.defaulthandler;

import com.chat.core.exception.HandlerException;
import com.chat.core.model.NPack;
import com.chat.core.model.URL;
import com.chat.core.model.UrlConstants;
import com.chat.core.model.netty.Request;
import com.chat.server.spi.handler.AbstractRequestHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;

import static com.chat.core.model.UrlConstants.ID_KEY;
import static com.chat.core.model.UrlConstants.MSG_PROTOCOL;

/**
 * 消息处协议理器
 *
 * @date:2020/2/17 14:36
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MessageRequestHandler extends AbstractRequestHandler {

    @Override
    public void handler(Request request, ChannelHandlerContext context) throws HandlerException {
        if (request.getProtocol().equals(UrlConstants.MSG_PROTOCOL)) {
            if (request.needACK()) {
                handlerAck(request, context);
            } else {
                handerNotAck(request);
            }
        } else {
            fireHandler(request, context);
        }
    }

    /**
     * 处理ACK消息
     */
    private static void handlerAck(Request request, ChannelHandlerContext context) {
        try {
            // todo
        } finally {
            String router = URL.encode(new URL(MSG_PROTOCOL, request.getHost(), request.getPort(), Collections.singletonMap(ID_KEY, request.getId())).toString());
            NPack pack = new NPack(router);
            context.writeAndFlush(pack);
        }
    }

    private static void handerNotAck(Request request) {
        System.out.println("收到消息 : "+new String(request.getBody()));
    }

}
