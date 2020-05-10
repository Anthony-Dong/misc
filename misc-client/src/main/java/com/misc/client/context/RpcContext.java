package com.misc.client.context;

import com.misc.client.future.ResponseFuture;
import com.misc.core.exception.TimeOutException;

import java.lang.reflect.Method;

/**
 * @date:2020/2/18 19:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface RpcContext {

    Object invoke(Class<?> clazz, Method method, long timeout, Object... args) throws TimeOutException;

    ResponseFuture invokeByAsync(Class<?> clazz, Method method, Object... args) throws TimeOutException;

    long getRequestTimeout();
}
