package com.misc.core.netty.rpc;

import com.misc.core.model.URL;
import com.misc.core.model.netty.Request;
import com.misc.core.model.rpc.RpcRequest;
import com.misc.core.model.rpc.RpcResponse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.misc.core.util.ExceptionUtils.*;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcConfig extends HashMap<String, Object> {

    private static final long serialVersionUID = 6915357945273844943L;


    private Map<String, Object> targetMap = new HashMap<>();

    private Map<String, Method> methodMap = new HashMap<>();

    public void addProxy(Object o) {
        targetMap.put(null, null);
    }

    public Object getTarget(RpcRequest request) {
        String serviceInterface = request.getUrl().getServiceInterface();
        if (serviceInterface == null || serviceInterface.length() == 0) {
            throw newNullPointerException("null");
        }
        Object target = targetMap.get(serviceInterface);
        if (target == null) {
            throw newNullPointerException("null");
        }
        return target;
    }


    public Method getMethod(Object target, RpcRequest request) {
        return null;
    }

}
