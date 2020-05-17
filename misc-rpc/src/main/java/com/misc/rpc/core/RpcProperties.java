package com.misc.rpc.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 属性
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcProperties extends HashMap<String, String> {
    private static final long serialVersionUID = -5942958653118649380L;
    private String methodName;

    void setMethodProperties(String key, String value) {
        super.put(methodName + "." + key, value);
    }

    void setProperties(String key, String value) {
        super.put(key, value);
    }

    public String getMethodProperties(String key) {
        return get(methodName + "." + key);
    }

    public String getProperties(String key) {
        return get(key);
    }

    public RpcProperties(int initialCapacity, float loadFactor, String methodName) {
        super(initialCapacity, loadFactor);
        this.methodName = methodName;
    }

    public RpcProperties(int initialCapacity, String methodName) {
        super(initialCapacity);
        this.methodName = methodName;
    }

    public RpcProperties(String methodName) {
        this.methodName = methodName;
    }

    public RpcProperties(Map<? extends String, ? extends String> m, String methodName) {
        super(m);
        this.methodName = methodName;
    }

}
