package com.misc.rpc.server;


import com.misc.core.exception.ConvertException;
import com.misc.core.exception.RpcException;
import com.misc.core.model.MiscPack;
import com.misc.core.model.Releasable;
import com.misc.core.model.URL;
import com.misc.core.serialization.SerializationFactory;
import com.misc.rpc.config.RpcConvertUtil;
import com.misc.rpc.core.InvokerInfo;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有的服务端调用信息
 */
public class RpcServerConfig extends SerializationFactory implements Releasable {

    /**
     * 这个是 invoker  key interfaceName
     */
    private final Map<String, InvokerInfo> invokeTargetMap = new HashMap<>();

    /**
     * 添加Invoker对象
     */
    public void addInvoker(Class<?> interfaceClass, Object target) {
        if (interfaceClass == null || target == null) {
            throw new NullPointerException();
        }
        if (!interfaceClass.isInterface()) {
            throw new RpcException(String.format("Rpc not support %s", interfaceClass));
        }
        if (!checkInterface(interfaceClass, target)) {
            throw new RpcException(String.format("target %s not implement interface %s", target, interfaceClass));
        }
        InvokerInfo invokerInfo = new InvokerInfo();
        invokerInfo.setInvokerClass(interfaceClass);
        invokerInfo.setInvokerTarget(target);
        Method[] methods = interfaceClass.getMethods();
        for (Method method : methods) {
            invokerInfo.addMethod(method);
        }
        invokeTargetMap.put(interfaceClass.getName(), invokerInfo);
    }


    private static boolean checkInterface(Class<?> interfaceClass, Object object) {
        Class<?>[] interfaces = object.getClass().getInterfaces();
        if (interfaces == null || interfaces.length == 0) {
            return false;
        }
        boolean has = false;
        for (Class<?> aClass : interfaces) {
            if (aClass == interfaceClass) {
                has = true;
                break;
            }
        }
        return has;
    }


    public InvokerInfo getInvokerInfo(String invokerInterface) {
        return invokeTargetMap.get(invokerInterface);
    }

    public RpcServerConfig() {

    }


    public RpcRequest convertMiscPackToRpcRequest(MiscPack pack) throws ConvertException {
        return RpcConvertUtil.convertMiscPackToRpcRequest(pack, this);
    }

    /**
     * server 端
     */
    public MiscPack convertRpcResponseToMiscPack(RpcResponse msg) throws ConvertException {
        URL url = msg.toURL();
        if (msg.getSerializer() != null && msg.getResult() != null) {
            byte[] serialize = msg.getSerializer().serialize(msg.getResult());
            return new MiscPack(url.toString(), serialize);
        }
        return new MiscPack(url.toString());
    }


    public RpcRequest convertFullHttpRequestToRpcRequest(FullHttpRequest msg) throws ConvertException {
        return null;
    }

    public FullHttpResponse convertRpcResponseToFullHttpResponse(RpcResponse msg) throws ConvertException {
        return null;
    }


    @Override
    public void release() {

    }
}
