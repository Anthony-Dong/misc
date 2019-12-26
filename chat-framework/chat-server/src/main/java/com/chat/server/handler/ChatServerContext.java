package com.chat.server.handler;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @date:2019/12/25 11:16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class ChatServerContext {

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
    protected void onStart() {

    }

    // 关闭上下文
    protected void onFail() {

    }

    /**
     * 注意  ChannelHandlerContext ,每一个客户端连接都会有一个 ChannelHandlerContext
     * 你可以自己去维护
     *
     * @param context ChannelHandlerContext
     */
    protected abstract void onRemove(ChannelHandlerContext context);

    /**
     * 注意  ChannelHandlerContext ,每一个客户端连接都会有一个 ChannelHandlerContext
     * 你可以自己去维护
     *
     * @param context ChannelHandlerContext
     */
    protected abstract void onRegister(ChannelHandlerContext context);
}
