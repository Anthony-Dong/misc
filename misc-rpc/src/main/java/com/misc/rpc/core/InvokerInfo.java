package com.misc.rpc.core;

import com.misc.core.util.StringUtils;
import com.misc.rpc.config.RpcConvertUtil;
import lombok.*;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * todo
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvokerInfo {

    // 空
    private static final String NULL_PARAMS = "[]";

    /**
     * 这个指的是接口
     */
    private Class<?> invokerClass;

    /**
     * 调用方
     */
    private Object invokerTarget;


    /**
     * key  methodName
     * value - key  java.lang.String,java.lang.Object ， 没有写NULL
     * value - obj  MethodInfo
     */
    private InvokerMethodMap invokerMethodMap = new InvokerMethodMap();


    public InvokerMethodInfo getInvokerMethodInfo(String methodName, String paramsType) {
        return invokerMethodMap.get(methodName + "." + (StringUtils.isEmpty(paramsType) ? null : paramsType));
    }


    public void addMethod(Method method) {
        if (method.getDeclaringClass() == Object.class) {
            return;
        }

        String methodName = method.getName();
        //
        InvokerMethodInfo methodInfo = new InvokerMethodInfo();
        methodInfo.setMethod(method);
        methodInfo.setReturnType(method.getReturnType());

        Class<?>[] parameterTypes = method.getParameterTypes();
        methodInfo.setParamsType(parameterTypes);

        String type = RpcConvertUtil.convertMethodParamsTypeToString(parameterTypes);
        if (StringUtils.isEmpty(type)) {
            invokerMethodMap.put(methodName, methodInfo);
            return;
        }
        invokerMethodMap.put(methodName + "." + type, methodInfo);
    }


    private static class InvokerMethodMap extends HashMap<String, InvokerMethodInfo> {
        private static final long serialVersionUID = -6184361591610192156L;


        @Override
        public InvokerMethodInfo get(Object key) {
            return super.get(key);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InvokerMethodInfo {

        private Method method;

        private Class<?>[] paramsType;

        private Class<?> returnType;
    }

}
