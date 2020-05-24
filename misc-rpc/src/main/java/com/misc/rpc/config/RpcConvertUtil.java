package com.misc.rpc.config;

import com.misc.core.exception.ConvertException;
import com.misc.core.model.MiscPack;
import com.misc.core.model.URL;
import com.misc.core.proto.TypeConstants;
import com.misc.core.serialization.Deserializer;
import com.misc.core.serialization.SerializationFactory;
import com.misc.core.serialization.Serializer;
import com.misc.core.util.StringUtils;
import com.misc.rpc.core.InvokerInfo;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import com.misc.rpc.server.RpcServerConfig;

/**
 * 转换的工具类
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcConvertUtil {

    private static final Object[] EMPTY_PARAMS = new Object[0];


    /**
     * 服务器端  将 MiscPack -> RpcRequest
     */
    public static RpcRequest convertMiscPackToRpcRequest(MiscPack miscPack, RpcServerConfig rpcServerConfig) throws ConvertException {
        if (StringUtils.isEmpty(miscPack.getRouter())) {
            throw new ConvertException("MiscPack.getRouter can not NULL");
        }
        URL url = URL.valueOf(miscPack.getRouter());
        if (!TypeConstants.validateType(url.getType())) {
            throw new ConvertException(String.format("The %s  Type not support", url));
        }

        if (url.getType().equals(TypeConstants.HEART_TYPE)) {
            return makeHeartRpcRequest(url);
        }

        // 这里只处理 请求方法
        RpcRequest rpcRequest = makeRpcRequestFromURL(url, rpcServerConfig);
        if (rpcRequest.getParamsType() != null && rpcRequest.getParamsType().length > 0) {
            // 序列化参数
            handlerRpcRequestParams(url, rpcServerConfig, rpcRequest, miscPack.getBody());
        }
        return rpcRequest;
    }

    /**
     * 服务端 心跳 heart
     */
    private static RpcRequest makeHeartRpcRequest(URL url) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setType(url.getType());
        rpcRequest.setPort(url.getPort());
        rpcRequest.setProtocol(url.getProtocol());
        rpcRequest.setHost(url.getHost());
        rpcRequest.setProperties(url.getAllParameters());
        return null;
    }

    /**
     * 将 rpcRequest -> MiscPack
     */
    public static MiscPack convertRpcRequestToMiscPack(RpcRequest rpcRequest) {
        URL url = rpcRequest.toURL();
        byte[] serialize = rpcRequest.getSerializer().serialize(rpcRequest.getParams());
        return new MiscPack(url.toString(), serialize);
    }

    /**
     * 从 URL 中提炼出 RpcRequest
     */
    private static RpcRequest makeRpcRequestFromURL(URL url, RpcServerConfig rpcServerConfig) {
        // 处理 接口
        String serviceInterface = url.getServiceInterface();
        String methodName = url.getMethodName();
        if (StringUtils.isEmpty(serviceInterface) || StringUtils.isEmpty(methodName)) {
            throw new ConvertException(String.format("The %s not handler because the url not has invokerClass or invokerMethod", url));
        }

        InvokerInfo invokerInfo = rpcServerConfig.getInvokerInfo(serviceInterface);
        if (invokerInfo == null) {
            throw new ConvertException(String.format("The %s not handler because not find invokerInfo %s", url, serviceInterface));
        }
        String methodParamsTypeString = url.getMethodParamsTypeString(methodName);
        InvokerInfo.InvokerMethodInfo methodInfo = invokerInfo.getInvokerMethodInfo(methodName, methodParamsTypeString);
        // 没有找到抛出异常
        if (methodInfo == null) {
            throw new ConvertException(String.format("The %s not handler because not find invokerInfo %s", url, serviceInterface));
        }

        // 协议类型
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setProtocol(url.getProtocol());
        rpcRequest.setHost(url.getHost());
        rpcRequest.setPort(url.getPort());
        rpcRequest.setType(url.getType());
        rpcRequest.setKey(url.getKey());

        // 这里只处理 请求方法
        rpcRequest.setParamsType(methodInfo.getParamsType());
        rpcRequest.setInvokeTarget(invokerInfo.getInvokerTarget());
        rpcRequest.setInvokeClazz(invokerInfo.getInvokerClass());
        rpcRequest.setInvokeMethod(methodInfo.getMethod());
        rpcRequest.setProperties(url.getAllParameters());
        return rpcRequest;
    }


    public static RpcResponse convertMiscPackToRpcResponse(MiscPack msg, SerializationFactory factory) {
        String router = msg.getRouter();
        URL url = URL.valueOf(router);
        return makeRpcResponseFromURL(url, factory, msg.getBody());
    }


    /**
     * 客户端
     */
    @SuppressWarnings("all")
    private static RpcResponse makeRpcResponseFromURL(URL url, SerializationFactory factory, byte[] body) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setPort(url.getPort());
        rpcResponse.setHost(url.getHost());
        rpcResponse.setKey(url.getKey());
        rpcResponse.setType(url.getType());
        // 添加返回类型参数
        try {
            Class<?> returnType = url.getReturnType();
            // 返回类型无的化，不需要编解码
            if (returnType == null) {
                return rpcResponse;
            }
            rpcResponse.setResultType(returnType);
        } catch (ClassNotFoundException e) {
            throw new ConvertException(String.format("The %s convert exception because  returnType class can not found", url));
        }

        // 添加序列化类型
        String deserializerClassName = url.getDeserializerClassName();
        String serializerClassName = url.getSerializerClassName();
        if (StringUtils.isEmpty(deserializerClassName) && StringUtils.isEmpty(serializerClassName)) {
            return rpcResponse;
        }
        try {
            Deserializer<Object, Class<?>> deserializerFromCache = factory.getDeserializerFromCache(deserializerClassName);
            Serializer<Object> serializerFromCache = factory.getSerializerFromCache(serializerClassName);
            rpcResponse.setDeserializer(deserializerFromCache);
            rpcResponse.setSerializer(serializerFromCache);
            Deserializer<Object, Class<?>> deserializer = rpcResponse.getDeserializer();
            Object result = deserializer.deserialize(body, rpcResponse.getResultType());
            rpcResponse.setResult(result);
            return rpcResponse;
        } catch (Exception e) {
            throw new ConvertException(String.format("The %s convert exception because  serializiation exception", url));
        }
    }

    /**
     * 添加属性
     */
    private static void addRpcRequestProperties(RpcRequest rpcRequest) {
        rpcRequest.setProperty(URL.Constants.METHOD_KEY, rpcRequest.getInvokeMethod().getName());
        rpcRequest.setProperty(URL.Constants.SERIALIZER_KEY, rpcRequest.getSerializer().getClass().getName());
        rpcRequest.setProperty(URL.Constants.DESERIALIZER_KEY, rpcRequest.getDeserializer().getClass().getName());
    }

    /**
     * 空检测自己处理
     */
    @SuppressWarnings("all")
    private static void handlerRpcRequestParams(URL url, RpcServerConfig rpcServerConfig, RpcRequest rpcRequest, byte[] body) {
        String serializerClassName = url.getSerializerClassName();
        String deserializerClassName = url.getDeserializerClassName();
        try {
            Deserializer<Object[], Class<?>[]> deserializer = rpcServerConfig.getDeserializerFromCache(deserializerClassName);
            Serializer<Object[]> serializer = rpcServerConfig.getSerializerFromCache(serializerClassName);
            rpcRequest.setSerializer(serializer);
            rpcRequest.setDeserializer(deserializer);
            if (body == null || body.length == 0) {
                return;
            }
            Object[] params = deserializer.deserialize(body, rpcRequest.getParamsType());
            rpcRequest.setParams(params);
        } catch (Exception e) {
            throw new ConvertException(String.format("The %s not handler because  deserializer error", url));
        }
    }

    /**
     * params 转成 string
     */
    public static String convertMethodParamsTypeToString(Class<?>[] params) {
        if (params == null || params.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < params.length - 1; x++) {
            builder.append(params[x].getName()).append(",");
        }
        builder.append(params[params.length - 1].getName());
        return builder.toString();
    }

}
