package com.chat.server.handler;

import com.chat.core.exception.ContextException;
import com.chat.core.netty.Constants;
import com.chat.core.netty.DiscardChannelHandlerContext;
import com.chat.core.util.ThreadPool;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @date:2019/12/25 11:16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class ChatServerContext {
    /**
     * 协议版本号
     */
    private final short version;

    /**
     * 上下文名称
     */
    private final String contextName;

    private ThreadPool threadPool;

    public final ThreadPool getThreadPool() {
        return threadPool;
    }

    /**
     * 这个是用来过封装的.
     */
    public final void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    /**
     * 默认上下文名称
     */
    private static final String DEFAULT_CONTEXT_NAME = "default-chat-server-name";

    /**
     * 默认版本号
     */
    private static final short DEFAULT_VERSION = Constants.PROTOCOL_VERSION;

    private static final ChannelHandlerContext NULL_Context = new DiscardChannelHandlerContext();


    /**
     * 可以拿到所有的context , 然后去进行逻辑处理
     */
    protected final Map<SocketAddress, ChannelHandlerContext> map;


    public ChatServerContext(String contextName, int version) {
        if (version > Short.MAX_VALUE || version < Short.MIN_VALUE) {
            throw new RuntimeException("Version range in -32768 with 32767 !");
        }
        this.version = (short) version;
        this.contextName = contextName;
        this.map = new ConcurrentHashMap<>();
    }

    public ChatServerContext(short version) {
        this(DEFAULT_CONTEXT_NAME, version);
    }


    public ChatServerContext() {
        this(DEFAULT_VERSION);
    }


    public final short getVersion() {
        return this.version;
    }

    public final Map<SocketAddress, ChannelHandlerContext> getUserContextMap() {
        return this.map;
    }


    public final ChannelHandlerContext getContext(SocketAddress address) {
        return map.getOrDefault(address, NULL_Context);
    }


    public final String getContextName() {
        return contextName;
    }

    // 启动上下文
    protected void onStart(InetSocketAddress address) throws ContextException {

    }

    // 关闭上下文
    protected void onFail(InetSocketAddress address) throws ContextException {

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

}
