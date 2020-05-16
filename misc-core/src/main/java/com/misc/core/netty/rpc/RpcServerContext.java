package com.misc.core.netty.rpc;

import com.misc.core.context.AbstractServerContext;
import com.misc.core.func.FunctionType;
import com.misc.core.model.URL;
import com.misc.core.model.rpc.RpcRequest;
import com.misc.core.model.rpc.RpcResponse;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.misc.core.util.ExceptionUtils.*;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcServerContext extends AbstractServerContext {

    private static final long serialVersionUID = 3166713580135567390L;

    public RpcServerContext(RpcConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
    }

    private final RpcConfig rpcConfig;


    /**
     * 发送 rpc response handler
     */
    public void receiveHandler(Channel channel, RpcRequest request) {
        Object target = rpcConfig.getTarget(request);
        Method method = rpcConfig.getMethod(target, request);
        try {
            Object result = method.invoke(target, request.getParams());
            channel.write(makeRpcResponse(result, request.getUrl()));
        } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
            throw newRuntimeException("调用%s发生异常:%s", target, e);
        }
    }

    public void sendHandler(RpcResponse response) {

    }

    private RpcResponse makeRpcResponse(Object result, URL url) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setUrl(new URL(FunctionType.RPC_TYPE.getType(), host, port, url.getPathKey()));
        rpcResponse.setResult(result);
        return rpcResponse;
    }

}
