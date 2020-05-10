package com.misc.server.rpc;

import com.misc.core.model.netty.ArgsUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 存储信息
 *
 * @date:2020/2/17 19:49
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcMap {

    private Map<String, Object> objectMap = new HashMap<>();

    private Map<String, Method> methodMap = new HashMap<>();

    /**
     * 添加接口
     */
    public void addService(Class<?> service, Object proxy) {
        // 前面一堆前置判断
        if (service == null || proxy == null) {
            throw new NullPointerException("代理类和接口不能为空");
        }
        if (!service.isInterface()) {
            throw new RuntimeException(String.format("%s不是接口.", service));
        }

        boolean flag = true;
        Class<?>[] interfaces = proxy.getClass().getInterfaces();
        if (interfaces == null || interfaces.length == 0) {
            throw new RuntimeException(String.format("代理对象%s没有实现%s接口.", proxy, service));
        }
        for (Class<?> anInterface : interfaces) {
            if (anInterface == service) {
                flag = false;
            }
        }
        if (flag) {
            throw new RuntimeException(String.format("代理对象%s没有实现%s接口.", proxy, service));
        }

        String name = service.getName();
        StringBuilder builder = new StringBuilder();
        builder.append(name);

        int len = builder.length();

        // 添加
        objectMap.put(service.getName(), proxy);
        for (Method method : service.getMethods()) {
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            if (method.isDefault()) {
                continue;
            }
            builder.append('.').append(ArgsUtil.getMethodName(method));
            methodMap.put(builder.toString(), method);
            builder.setLength(len);
        }
    }

    public Map<String, Object> getObjectMap() {
        return objectMap;
    }


    public Map<String, Method> getMethodMap() {
        return methodMap;
    }
}
