package com.chat.client.hander;

import com.chat.client.netty.ChatClient;
import com.chat.core.context.Context;
import com.chat.core.model.netty.Response;
import com.chat.core.netty.Constants;
import com.chat.core.netty.NettyProperties;
import com.chat.core.netty.SerializableType;
import com.chat.core.util.NetUtils;
import com.chat.core.util.ThreadPool;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CountDownLatch;

import static com.chat.core.netty.PropertiesConstant.*;
import static com.chat.core.netty.PropertiesConstant.CLIENT_PORT;

/**
 * 客户端上下文
 *
 * @date:2019/12/24 22:51
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class ChatClientContext implements Context {

    /**
     * 一些必要属性
     */
    private NettyProperties properties = new NettyProperties();

    /**
     * 全局唯一的context 对象
     */
    protected ChannelHandlerContext context;

    /**
     * 默认的心跳时间
     */
    private int heartInterval = -1;

    // 版本号, 默认值
    protected short version = Constants.PROTOCOL_VERSION;


    private SerializableType serializableType = Constants.DEFAULT_SERIALIZABLE_TYPE;

    // 上下文名称
    private String contextName = "chat-server";


    protected String hostName = Constants.DEFAULT_HOST;


    protected String realHostName = NetUtils.filterLocalHost(hostName);

    protected int port = Constants.DEFAULT_PORT;


    private CountDownLatch latch = new CountDownLatch(1);
    /**
     * 线程池
     */
    private ThreadPool threadPool = new ThreadPool(1, 0, "Netty-Worker");

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
    protected abstract void onRead(Response context);

    public CountDownLatch getLatch() {
        return latch;
    }

    public final String getContextName() {
        return this.contextName;
    }

    public final short getVersion() {
        return this.version;
    }

    public final void setVersion(Short version) {
        properties.setShort(CLIENT_PROTOCOL_VERSION, version);
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


    public final NettyProperties getProperties() {
        return properties;
    }

    public final SerializableType getSerializableType() {
        return serializableType;
    }

    public final void setSerializableType(SerializableType type) {
        properties.setByte(CLIENT_PROTOCOL_TYPE, type.getCode());
        this.serializableType = type;
    }


    public final String getHostName() {
        return hostName;
    }

    public final void setHostName(String hostName) {
        properties.setString(CLIENT_HOST, hostName);
        this.realHostName = NetUtils.filterLocalHost(hostName);
        this.hostName = hostName;
    }

    public final int getPort() {
        return port;
    }

    public final void setPort(int port) {
        properties.setInt(CLIENT_PORT, port);
        this.port = port;
    }


    // 为了关闭掉服务器
    protected ChatClient client;

    public final void setClient(ChatClient client) {
        this.client = client;
    }

    // 清空引用
    private void relese() {
        if (client != null) {
            client.shutDown();
            client = null;
        }
    }

    /**
     * 收到事件后, 先释放掉 client.
     */
    @Override
    public void onShutdown() {
        try {
            relese();
        } finally {
            onClose();
        }
    }

    @Override
    public void onBootstrap() {
        onStart();
    }

    protected abstract void onStart();

    protected abstract void onClose();
}
