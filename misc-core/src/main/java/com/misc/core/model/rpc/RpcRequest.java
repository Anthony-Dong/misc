package com.misc.core.model.rpc;

import com.alibaba.fastjson.JSON;
import com.misc.core.model.MiscRequest;

import java.lang.reflect.Method;

/**
 * Rpc调用的参数
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcRequest extends MiscRequest {

    private static final long serialVersionUID = -3967657981609594301L;
    /**
     * 接口名称
     */
    private Class<?> invokeInterface;

    /**
     * 方法
     */
    private Method method;

    /**
     * 防止方法重载
     */
    private Class<?>[] ParamsType;

    /**
     * 参数
     */
    private Object[] Params;


    private static final String TIMEOUT = "timeout";

    private static final String METHOD = "method";

    private static final String INTERFACE = "interface";

    /**
     * 超时时间
     */
    private long timeout;

    /**
     * 重试
     */
    private int retry;

    /**
     * 降级接口
     */
    private Class<?> rollbackClass;

    public RpcRequest setTimeout(long timeout) {
        url.addParameter(TIMEOUT, timeout);
        return this;
    }


    public RpcRequest setInterface(Class<?> clazz) {
        if (!clazz.isInterface()) {
            throw new RuntimeException(String.format("%s is not interface", clazz));
        }
        url.setServiceInterface(clazz.getName());
        this.invokeInterface = clazz;
        return this;
    }

    public RpcRequest setMethod(Method method) {
        if (this.invokeInterface == null) {
            throw new RuntimeException("Is not set invoke interface");
        }
        url.addParameter(METHOD, method.getName());
        this.method = method;
        this.ParamsType = method.getParameterTypes();
        return this;
    }


    public Method getMethod() {
        return method;
    }

    public Class<?>[] getParamsType() {
        return ParamsType;
    }

    public Object[] getParams() {
        return Params;
    }

    public String exportUrl() {
        return url.toString();
    }


    public byte[] exprotBody() {
        return JSON.toJSONBytes(this.Params);
    }

    public void setParams(Object[] params) {
        Params = params;
    }

    public void setInterfaceName(String interfaceName) {
        url.setServiceInterface(interfaceName);
    }

    @Override
    public void release() {

    }
}
