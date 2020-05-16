package com.misc.core.netty.rpc;

import com.misc.core.context.AbstractServerContext;
import com.misc.core.exception.HandlerException;
import com.misc.core.func.FunctionType;
import com.misc.core.model.URL;
import com.misc.core.model.rpc.RpcRequest;
import com.misc.core.model.rpc.RpcResponse;
import com.misc.core.netty.ChannelHandler;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.misc.core.util.ExceptionUtils.newRuntimeException;

/**
 * rpc 的处理器
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcServerChannelHandler extends AbstractServerContext implements ChannelHandler<RpcRequest, RpcResponse> {
    private static final long serialVersionUID = 3606214454920870977L;

    /**
     * rpc - config
     */
    private final RpcConfig rpcConfig;

    public RpcServerChannelHandler() {
        rpcConfig = (RpcConfig) config;
    }


    @Override
    public void connected(Channel channel) throws HandlerException {
        addChannel(channel);
    }


    @Override
    public void disconnected(Channel channel) throws HandlerException {
        removeChannel(channel);
    }

    @Override
    public void sent(Channel channel, RpcResponse message) throws HandlerException {
        sendHandler(message);
    }

    @Override
    public void received(Channel channel, RpcRequest message) throws HandlerException {
        receiveHandler(channel, message);
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws HandlerException {

    }


    /**
     * 发送 rpc response handler
     */
    private void receiveHandler(Channel channel, RpcRequest request) {
        Object target = rpcConfig.getTarget(request);
        Method method = rpcConfig.getMethod(target, request);
        try {
            Object result = method.invoke(target, request.getParams());
            channel.write(makeRpcResponse(result, request.getUrl()));
        } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
            throw newRuntimeException("调用%s发生异常:%s", target, e);
        }
    }

    private void sendHandler(RpcResponse response) {

    }

    private RpcResponse makeRpcResponse(Object result, URL url) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setUrl(new URL(FunctionType.RPC_TYPE.getType(), host, port, url.getPathKey()));
        rpcResponse.setResult(result);
        return rpcResponse;
    }
}
