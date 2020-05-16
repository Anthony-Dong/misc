package com.misc.rpc.config;


import com.misc.core.model.MiscPack;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import com.misc.rpc.core.RpcServiceConfig;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.misc.core.util.ExceptionUtils.*;

/**
 * todo
 */
public class RpcConfig extends HashMap<String, Object> {

    private static final long serialVersionUID = 6915357945273844943L;


    private Map<RpcServiceConfig, Object> targetMap = new HashMap<>();

    private Map<String, Method> methodMap = new HashMap<>();

    public void addProxy(Object o) {
        targetMap.put(null, null);
    }

    public Object getTarget(RpcServiceConfig rpcServiceConfig) {
        Object target = targetMap.get(rpcServiceConfig);
        if (target == null) {
            throw newNullPointerException("null");
        }
        return target;
    }


    public Method getMethod(Object target, RpcRequest request) {
        return null;
    }


    public RpcRequest convertMiscPackToRpcRequest(MiscPack pack) {

        return null;
    }

    public MiscPack convertRpcResponseToMiscPack(RpcResponse msg) {
        return null;
    }


    public RpcRequest convertFullHttpRequestToRpcRequest(FullHttpRequest msg) {
        return null;
    }

    public FullHttpResponse convertRpcResponseToFullHttpResponse(RpcResponse msg) {
        return null;
    }
}
