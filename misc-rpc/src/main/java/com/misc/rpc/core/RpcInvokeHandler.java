package com.misc.rpc.core;

import com.misc.core.exception.RpcException;
import com.misc.core.exception.TimeOutException;

import java.lang.reflect.Method;

/**
 * @date:2020/2/18 19:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface RpcInvokeHandler {
    Object invoke(Class<?> clazz, Method method, Object... args) throws RpcException;
}
