package com.misc.rpc.core;

import com.misc.core.exception.ConvertException;
import com.misc.core.model.MiscPack;
import com.misc.core.model.URL;
import com.misc.core.serialization.JsonObjectArraySerialization;
import com.misc.core.test.EchoService;
import com.misc.core.util.StringUtils;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;

public class RpcRequestTest {

    private static final JsonObjectArraySerialization DEFAULT_SERIALIZATION = new JsonObjectArraySerialization();
    private static final Object[] EMPTY_PARAMS = new Object[0];
    private static final Class<?>[] EMPTY_PARAMS_TYPE = new Class<?>[0];

    @Test
    public void release() throws ClassNotFoundException {
        RpcRequest rpcRequest = new RpcRequest();
        Class<EchoService> clazz = EchoService.class;
        rpcRequest.setInvokeClazz(clazz);

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("hash")) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                rpcRequest.setInvokeMethod(method);
                rpcRequest.setParamsType(parameterTypes);
            }
        }
        JsonObjectArraySerialization serialization = new JsonObjectArraySerialization();
        rpcRequest.setDeserializer(serialization);
        rpcRequest.setSerializer(serialization);
        rpcRequest.setParams(new Object[]{"hello world"});
        System.out.println(rpcRequest.getInvokeClazz().getName());

        rpcRequest.setMethodProperty("timeout", "1");
        rpcRequest.setMethodProperty("version", "2");

        MiscPack make = make(rpcRequest);

        System.out.println(make);


        URL url = URL.valueOf(make.getRouter());

        System.out.println("url.getProtocol() = " + url.getProtocol());
        System.out.println("url.getHost() = " + url.getHost());
        System.out.println("url.getPort() = " + url.getPort());
        System.out.println("url.getDeserializerClassName() = " + url.getDeserializerClassName());
        System.out.println("url.getServiceInterface() = " + url.getServiceInterface());
        System.out.println("url.getServiceInterface() = " + url.getSerializerClassName());
        System.out.println("url.getMethodParameter(\"hash\",\"timeout\") = " + url.getMethodParameter("hash", "timeout"));

        Class<?>[] hashes = url.getMethodParamsType("hash");
        hashes[0]=EchoService.class;
        for (Class<?> hash : hashes) {
            System.out.println(hash.getName());
        }

        hashes=null;
        Class<?>[] hashes2 = url.getMethodParamsType("hash");
        for (Class<?> hash : hashes2) {
            System.out.println(hash.getName());
        }
    }

    /**
     * 转换
     *
     * @param rpcRequest
     * @return
     */
    public static MiscPack make(RpcRequest rpcRequest) {
        if (rpcRequest.getInvokeClazz() == null) {
            throw new ConvertException("RpcRequest的invokeClazz不能为空");
        }

        if (rpcRequest.getInvokeMethod() == null) {
            throw new ConvertException("RpcRequest的invokeMethod不能为空");
        }


        if (rpcRequest.getSerializer() == null && rpcRequest.getDeserializer() != null) {
            throw new ConvertException("RpcRequest的serializer和serializer必须指定");
        }

        if (rpcRequest.getDeserializer() == null && rpcRequest.getSerializer() != null) {
            throw new ConvertException("RpcRequest的serializer和serializer必须指定");
        }

        // 设置序列化类型
        if (rpcRequest.getSerializer() == null && rpcRequest.getDeserializer() == null) {
            rpcRequest.setSerializer(DEFAULT_SERIALIZATION);
            rpcRequest.setDeserializer(DEFAULT_SERIALIZATION);
        }

        // 添加参数
        addRpcRequestProperties(rpcRequest);


        // 添加方法参数类型
        String paramsType = convertMethodParamsTypeToString(rpcRequest.getParamsType());
        if (StringUtils.isNotEmpty(paramsType)) {
            rpcRequest.setMethodProperty(URL.Constants.PARAMS_KEY, paramsType);
        }

        URL misc = new URL("misc", "0.0.0.0", 1234, rpcRequest.getInvokeClazz().getName(), rpcRequest.getProperties());
        byte[] serialize = rpcRequest.getSerializer().serialize(rpcRequest.getParams() == null ? EMPTY_PARAMS : rpcRequest.getParams());
        return new MiscPack(misc.toString(), serialize);
    }

    /**
     * 添加属性
     */
    private static void addRpcRequestProperties(RpcRequest rpcRequest) {
        Map<String, String> properties = rpcRequest.getProperties();
        properties.put(URL.Constants.METHOD_KEY, rpcRequest.getInvokeMethod().getName());
        properties.put(URL.Constants.SERIALIZER_KEY, rpcRequest.getSerializer().getClass().getName());
        properties.put(URL.Constants.DESERIALIZER_KEY, rpcRequest.getDeserializer().getClass().getName());
    }

    /**
     * 将 MethodParamsType 转换成 type1,type2,type3 , 空则返回 NULL
     *
     * @param params
     * @return
     */
    private static String convertMethodParamsTypeToString(Class<?>[] params) {
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