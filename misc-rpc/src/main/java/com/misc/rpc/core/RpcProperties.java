package com.misc.rpc.core;

import com.misc.core.commons.Constants;
import com.misc.core.exception.RpcException;
import com.misc.core.model.URL;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rpc属性
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcProperties extends HashMap<String, String> {
    private static final long serialVersionUID = -5942958653118649380L;
    private Method method;

    /**
     * 给方法设置属性
     */
    public void setMethodProperties(String key, String value) {
        if (method == null) {
            put(key, value);
            return;
        }
        super.put(method.getName() + "." + key, value);
    }

    /**
     * 设置普通属性
     *
     * @param key
     * @param value
     */
    public void setProperties(String key, String value) {
        super.put(key, value);
    }

    public String getMethodProperties(String key) {
        if (method == null) {
            return get(key);
        }
        return get(method.getName() + "." + key);
    }

    public String getProperties(String key) {
        return get(key);
    }

    public RpcProperties(int initialCapacity, float loadFactor, Method method) {
        super(initialCapacity, loadFactor);
        this.method = method;
    }

    public RpcProperties(int initialCapacity, Method method) {
        super(initialCapacity);
        this.method = method;
    }

    public RpcProperties(Method method) {
        this.method = method;
    }

    RpcProperties() {
    }

    public RpcProperties(Map<? extends String, ? extends String> m, Method method) {
        super(m);
        this.method = method;
    }


    public boolean needAck() {
        return getOrDefault(URL.Constants.ACK_KEY, "1").equals("1");
    }

    public void setAck(boolean needAck) {
        put(URL.Constants.ACK_KEY, needAck ? "1" : "0");
    }


    private static final String DEFAULT_TIME_OUT = String.valueOf(Constants.DEFAULT_REQUEST_TIMEOUT);

    /**
     * 默认超时时间
     */
    public long getTimeOut() {
        return Long.parseLong(getOrDefault(URL.Constants.TIMEOUT_KEY, DEFAULT_TIME_OUT));
    }

    public void setTimeOut(long timeOut) {
        put(URL.Constants.TIMEOUT_KEY, String.valueOf(timeOut));
    }

    public Method getMethod() {
        return method;
    }


    private Class<?> fallBackClass;

    private static final Map<Class, Object> fallBackObj = new ConcurrentHashMap<>();

    public void setFallBackClass(Class<?> fallBackClass) {
        if (method == null) {
            throw new RpcException("RpcProperties setFallBackClass the method can not be null");
        }
        Class<?> declaringClass = method.getDeclaringClass();
        Class<?>[] interfaces = fallBackClass.getInterfaces();

        boolean flag = false;
        for (Class<?> anInterface : interfaces) {
            if (anInterface == declaringClass) {
                flag = true;
                this.fallBackClass = fallBackClass;
            }
        }
        if (!flag) {
            throw new RpcException(String.format("RpcProperties setFallBackClass the fallBackClass must instance of %s", declaringClass));
        }

        try {
            Object obj = this.fallBackClass.newInstance();
            fallBackObj.put(fallBackClass, obj);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RpcException(String.format("RpcProperties  setFallBackClass the %s new instance fail", fallBackClass));
        }
    }

    /**
     * 获取 fall back
     */
    public Object getFallBack() {
        if (method == null) {
            throw new RpcException("RpcProperties getFallBack the method can not be null");
        }

        if (fallBackClass == null) {
            throw new RpcException("RpcProperties getFallBack the fallBackClass can not be null");
        }

        Object obj = fallBackObj.get(fallBackClass);
        // 由于插入的时候，已经初始化好了
        if (obj == null) {
            throw new RpcException(String.format("RpcProperties getFallBack the %s instance fail", fallBackClass));
        }
        return obj;
    }

    public Class<?> getFallBackClass() {
        return fallBackClass;
    }
}
