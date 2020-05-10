package com.misc.server.spi.handler;

import com.misc.core.exception.HandlerException;
import com.misc.core.model.netty.Request;
import io.netty.channel.ChannelHandlerContext;

/**
 * 这是一个执行链 , 不断传给下一个 , 这种模式叫做责任链模式
 *
 * @date:2020/2/17 14:10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class AbstractRequestHandler implements RequestHandler {

    // 初始化
    private AbstractRequestHandler next = null;

    /**
     * 需要实现的接口
     */
    @Override
    public abstract void handler(Request request, ChannelHandlerContext context) throws HandlerException;

    /**
     * 传递给下一个
     *
     * @throws HandlerException
     */
    protected final void fireHandler(Request request, ChannelHandlerContext context) throws HandlerException {
        if (next != null) {
            next.handler(request, context);
        }
    }

    AbstractRequestHandler getNext() {
        return next;
    }

    void setNext(AbstractRequestHandler next) {
        this.next = next;
    }
}
