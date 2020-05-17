package com.misc.rpc.core;

import com.misc.core.exception.ProxyException;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;

/**
 * 代理类
 *
 * @date:2020/2/18 19:27
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcProxy<T> implements InvocationHandler {

    /**
     * 代理接口
     */
    private final RpcInvokeHandler invokeHandler;

    /**
     * 代理接口
     */
    private final Class<T> inter;


    private RpcProxy(RpcInvokeHandler invokeHandler, Class<T> inter) {
        this.invokeHandler = invokeHandler;
        this.inter = inter;
    }


    @SuppressWarnings("all")
    public static <T> T newInstance(Class<T> inter, RpcInvokeHandler invokeHandler) throws ProxyException {
        if (!inter.isInterface()) {
            throw new ProxyException(String.format("The %s not be a interface", inter));
        }
        if (!inter.isInterface()) return null;
        Class<?> proxyClass = Proxy.getProxyClass(Thread.currentThread().getContextClassLoader(), inter);
        RpcProxy<T> rpcProxy = new RpcProxy<>(invokeHandler, inter);
        try {
            return (T) proxyClass.getConstructor(InvocationHandler.class).newInstance(rpcProxy);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ProxyException(e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        if (method.isDefault()) {
            return invokeDefaultMethod(proxy, method, args);
        }
        return invokeHandler.invoke(inter, method, args);
    }

    /**
     * 引用自 mybatis的 mapperproxy
     *
     * @throws Throwable 异常
     */
    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
            throws Throwable {
        final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class, int.class);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        return constructor
                .newInstance(declaringClass,
                        MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                                | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
                .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    /**
     * 修改后的toString方法
     */
    @Override
    public String toString() {
        return inter.getName() + "@" + Integer.toHexString(this.hashCode());
    }
}
