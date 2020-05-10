package com.misc.server.handler;

import com.misc.core.context.AbstractContext;
import com.misc.core.exception.ContextException;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MiscServerContext 上下文对象 , 优先级最高
 *
 * @date:2019/12/25 11:16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class MiscServerContext extends AbstractContext {


    private static final long serialVersionUID = -1787625994967298766L;

    /**
     * 可以拿到所有的context , 然后去进行逻辑处理
     */
    protected final Map<SocketAddress, ChannelHandlerContext> map = new ConcurrentHashMap<>();


    /**
     * 获取 客户端对象
     */
    public final Map<SocketAddress, ChannelHandlerContext> getUserContextMap() {
        return this.map;
    }


    /**
     * 可以通过 address 获取context对象 , 记住{@link InetSocketAddress#hashCode()} hashcode是重写过得
     */
    public final ChannelHandlerContext getContext(SocketAddress address) {
        return map.get(address);
    }


    /**
     * 启动server服务器
     */
    final void onStart(InetSocketAddress address) throws ContextException {
        try {
            if (registryService != null) {
                registryService.register(address, version);
            }
        } finally {
            onBootstrap();
        }
    }


    /**
     * 关闭server服务器
     */
    final void onFail(InetSocketAddress address) throws ContextException {
        try {
            if (registryService != null) {
                registryService.unregister(address, version);
            }
        } finally {
            onShutdown();
        }
    }


    /**
     * 注意  ChannelHandlerContext ,每一个客户端连接都会有一个 ChannelHandlerContext
     * 你可以自己去维护
     *
     * @param context ChannelHandlerContext
     */
    final void onRemove(ChannelHandlerContext context) throws ContextException {
        try {
            this.onChannelRemove(context);
        } finally {
            map.remove(context.channel().remoteAddress());
            context.close();
        }
    }

    protected void onChannelRemove(ChannelHandlerContext context) {

    }

    /**
     * 注意  ChannelHandlerContext ,每一个客户端连接都会有一个 ChannelHandlerContext
     * 你可以自己去维护
     *
     * @param context ChannelHandlerContext
     */
    final void onRegister(ChannelHandlerContext context) throws ContextException {
        try {
            this.onChannelRegistered(context);
        } finally {
            map.put(context.channel().remoteAddress(), context);
        }
    }

    /**
     * 可重写
     *
     * @param context
     */
    protected void onChannelRegistered(ChannelHandlerContext context) {

    }
}
