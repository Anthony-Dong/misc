package com.misc.rpc.client;

import com.misc.core.exception.HandlerException;
import com.misc.core.exception.TimeOutException;
import com.misc.core.netty.NettyEventListener;
import com.misc.core.proto.ProtocolType;
import com.misc.core.proto.TypeConstants;
import com.misc.rpc.core.*;
import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleStateEvent;

import java.lang.reflect.Method;

/**
 * 客户端处理器
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcClientHandler implements NettyEventListener<RpcResponse, RpcRequest> {

    private FallBack fallBack = response -> logger.error("the response {} can not handler", response);

    @Override
    public void connected(Channel channel) throws HandlerException {
        logger.info("connected server {} success", channel.remoteAddress());
    }

    @Override
    public void disconnected(Channel channel) throws HandlerException {
        logger.info("disconnected server {} success", channel.remoteAddress());
    }

    @Override
    public void sent(Channel channel, RpcRequest message) throws HandlerException {

    }

    @Override
    public void received(Channel channel, RpcResponse message) throws HandlerException {
        RpcFuture.received(message, fallBack);
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws HandlerException {
        logger.error("handler exception caught {}", exception.toString());
    }

    @Override
    public void eventTriggered(Channel channel, Object event) throws HandlerException {
        // 心跳处理器
        if (event instanceof IdleStateEvent) {
            RpcRequest request = new RpcRequest();
            request.setType(TypeConstants.HEART_TYPE);
            channel.writeAndFlush(request);
        }
    }
}
