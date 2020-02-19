package com.chat.server.handler;

import com.chat.core.context.Context;
import com.chat.core.exception.ContextException;
import com.chat.core.netty.Constants;
import com.chat.core.register.RegistryService;
import com.chat.core.util.ThreadPool;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ChatServerContext 上下文对象 , 优先级最高
 *
 * @date:2019/12/25 11:16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class ChatServerContext implements Context {

    /**
     * 协议版本号
     */
    private short version = Constants.PROTOCOL_VERSION;

    /**
     * 上下文名称
     */
    private String contextName = "server-context";

    /**
     * 服务器地址
     */
    private InetSocketAddress address;
    /**
     * 线程池
     */
    private ThreadPool threadPool;

    /**
     * 可以拿到所有的context , 然后去进行逻辑处理
     */
    protected final Map<SocketAddress, ChannelHandlerContext> map = new ConcurrentHashMap<>();

    /**
     * 注册中心
     */
    private RegistryService registryService;


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

    public final InetSocketAddress getAddress() {
        return address;
    }

    public final void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public final ThreadPool getThreadPool() {
        return threadPool;
    }

    public final void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public final void setVersion(short version) {
        this.version = version;
    }

    public final short getVersion() {
        return this.version;
    }

    public final String getContextName() {
        return contextName;
    }

    public final void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public final RegistryService getRegistryService() {
        return registryService;
    }

    public final void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }
}
