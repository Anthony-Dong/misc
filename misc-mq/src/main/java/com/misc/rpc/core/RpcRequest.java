package com.misc.rpc.core;


import lombok.Data;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * Rpc调用的参数
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Data
@Setter
public class RpcRequest {

    private static final long serialVersionUID = -3967657981609594301L;

    /**
     * 接口名称
     */
    private Class<?> serverInterface;

    /**
     * 调用对象
     */
    private Object serviceTarget;

    /**
     * 方法
     */
    private Method method;

    /**
     * 方法类型
     */
    private Class<?>[] ParamsType;


    /**
     * 方法参数
     */
    private Object[] Params;


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


    private static final String TIMEOUT = "timeout";

    private static final String METHOD = "method";

    private static final String INTERFACE = "interface";

}
