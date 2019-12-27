package com.chat.server.handler;

import com.chat.core.exception.ContextException;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @date:2019/12/25 11:16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class ChatServerContext {

    public static final ChatServerContext NULL = new ChatServerContext() {
        @Override
        protected void onStart(InetSocketAddress address) throws ContextException {
            super.onStart(address);
        }

        @Override
        protected void onFail(InetSocketAddress address) throws ContextException {
            super.onFail(address);
        }

        /**
         * 注意  ChannelHandlerContext ,每一个客户端连接都会有一个 ChannelHandlerContext
         * 你可以自己去维护
         *
         * @param context ChannelHandlerContext
         */
        @Override
        protected void onRemove(ChannelHandlerContext context) throws ContextException {
            super.onRemove(context);
        }

        /**
         * 注意  ChannelHandlerContext ,每一个客户端连接都会有一个 ChannelHandlerContext
         * 你可以自己去维护
         *
         * @param context ChannelHandlerContext
         */
        @Override
        protected void onRegister(ChannelHandlerContext context) throws ContextException {
            super.onRegister(context);
        }
    };


    private final String contextName;
    /**
     * 可以拿到所有的context , 然后去进行逻辑处理
     */
    protected final Map<String, ChannelHandlerContext> map;

    public ChatServerContext(String contextName) {
        this.contextName = contextName;
        this.map = new ConcurrentHashMap<>();
    }

    public ChatServerContext() {
        this("default-chat-server-name");
    }

    public final Map<String, ChannelHandlerContext> getUserContextMap() {
        return this.map;
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
    protected void onRemove(ChannelHandlerContext context) throws ContextException {

    }

    /**
     * 注意  ChannelHandlerContext ,每一个客户端连接都会有一个 ChannelHandlerContext
     * 你可以自己去维护
     *
     * @param context ChannelHandlerContext
     */
    protected void onRegister(ChannelHandlerContext context) throws ContextException {

    }
}
