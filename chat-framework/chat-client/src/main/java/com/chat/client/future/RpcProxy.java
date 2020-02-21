package com.chat.client.future;

import com.chat.client.context.RpcContext;
import com.chat.core.exception.ProxyException;
import com.chat.core.netty.Constants;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.concurrent.TimeUnit;

/**
 * 代理类
 *
 * @date:2020/2/18 19:27
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcProxy<T> implements InvocationHandler {

    private final RpcContext context;

    private final Class<T> inter;

    private RpcProxy(RpcContext context, Class<T> inter) {
        this.context = context;
        this.inter = inter;
    }

    @SuppressWarnings("all")
    public static <T> T newInstance(Class<T> inter, RpcContext context) throws ProxyException {
        if (!inter.isInterface()) {
            throw new ProxyException(String.format("%s 不是接口类", inter));
        }
        if (!inter.isInterface()) return null;
        Class<?> proxyClass = Proxy.getProxyClass(Thread.currentThread().getContextClassLoader(), inter);
        RpcProxy<T> rpcProxy = new RpcProxy<>(context, inter);
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
        // Constants.DEFAULT_TIMEOUT
        return context.invoke(inter, method, Constants.DEFAULT_TIMEOUT, args);
    }

    /**
     * 引用自 mybatis的mapperproxy
     *
     * @throws Throwable
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
