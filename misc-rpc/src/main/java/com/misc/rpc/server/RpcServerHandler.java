package com.misc.rpc.server;

import com.misc.core.exception.HandlerException;
import com.misc.core.netty.NettyEventListener;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationTargetException;

/**
 * rpc 处理器
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcServerHandler implements NettyEventListener<RpcRequest, RpcResponse> {

    @Override
    public void connected(Channel channel) throws HandlerException {
        logger.info("{} connect", channel.remoteAddress());
    }

    @Override
    public void disconnected(Channel channel) throws HandlerException {
        logger.info("{} disconnected", channel.remoteAddress());
    }

    @Override
    public void sent(Channel channel, RpcResponse message) throws HandlerException {
        //
    }

    @Override
    public void received(Channel channel, RpcRequest request) throws HandlerException {
        logger.info("{} receive", request);
        try {
            Object invoke = request.getInvokeMethod().invoke(request.getInvokeTarget(), request.getParams());
            RpcResponse response = new RpcResponse();
            response.setResult(invoke);
            response.setResultType(request.getInvokeMethod().getReturnType());
            response.setKey(request.getKey());
            request.release();
            channel.writeAndFlush(response).addListener(future -> System.out.println(future.isSuccess()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws HandlerException {
        System.out.println("exception  " + exception.getMessage());
    }
}
