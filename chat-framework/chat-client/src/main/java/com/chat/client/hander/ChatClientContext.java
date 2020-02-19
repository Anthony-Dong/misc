package com.chat.client.hander;

import com.chat.core.context.Context;
import com.chat.core.model.netty.Response;
import com.chat.core.netty.Constants;
import com.chat.core.util.ThreadPool;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * 客户端上下文
 *
 * @date:2019/12/24 22:51
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class ChatClientContext implements Context {

    /**
     * 全局唯一的context 对象
     */
    protected ChannelHandlerContext context;

    /**
     * 默认的心跳时间
     */
    private int heartInterval = -1;

    // 版本号, 默认值
    private short version = Constants.PROTOCOL_VERSION;

    // 上下文名称
    private String contextName = "chat-server";

    public InetSocketAddress address;

    private final CountDownLatch latch = new CountDownLatch(1);


    private ThreadPool threadPool;

    /**
     * 阻塞过程
     *
     * @return ChannelHandlerContext
     */
    public final ChannelHandlerContext getContext() {
        return context;
    }

    /**
     * 只允许开发者设置
     *
     * @param context ChannelHandlerContext
     */
    final void setContext(ChannelHandlerContext context) {
        this.context = context;
    }


    /**
     * 客户端接收到信息
     *
     * @param context NPack
     */
    protected void onReading(Response context) {

    }


    public CountDownLatch getLatch() {
        return latch;
    }

    public final InetSocketAddress getAddress() {
        return address;
    }

    public final void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public final String getContextName() {
        return this.contextName;
    }

    public final short getVersion() {
        return this.version;
    }

    public final void setVersion(short version) {
        this.version = version;
    }

    public final void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public final ThreadPool getThreadPool() {
        return threadPool;
    }

    public final void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public final int getHeartInterval() {
        return heartInterval;
    }

    public final void setHeartInterval(int heartInterval) {
        this.heartInterval = heartInterval;
    }

    public static void init(ChatClientContext context) {
        // context 优先级高
        if (context.getAddress() == null) {
            context.setAddress(new InetSocketAddress(Constants.DEFAULT_HOST, Constants.DEFAULT_PORT));
        }
        // 一个线程就可以了, 防止阻塞解码线程
        if (context.getThreadPool() == null) {
            context.setThreadPool(new ThreadPool(1, 0, "Netty-Worker"));
        }
        if (context.getHeartInterval() == -1) {
            context.setHeartInterval(Constants.DEFAULT_HEART_INTERVAL);
        }
    }
}
