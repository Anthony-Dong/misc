package com.chat.client.hander;

import com.chat.core.model.NPack;
import com.chat.core.netty.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 客户端上下文
 *
 * @date:2019/12/24 22:51
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class ChatClientContext {

    /**
     * 全局唯一的context 对象
     */
    private volatile ChannelHandlerContext context;

    /**
     * 版本号, 默认值是 {@link Constants.PROTOCOL_VERSION}
     */
    private final short version;

    /**
     * 默认名
     */
    private static final String DEFAULT_CONTEXT_NAME = "chat-server-name";

    /**
     * 默认版本号
     */
    private static final short DEFAULT_VERSION = Constants.PROTOCOL_VERSION;

    // 上下文名称
    private final String contextName;


    public final String getContextName() {
        return contextName;
    }


    public final short getVersion() {
        return version;
    }


    public ChatClientContext(String contextName, short version) {
        this.contextName = contextName;
        this.version = version;
    }

    public ChatClientContext(String contextName) {
        this(contextName, DEFAULT_VERSION);
    }

    public ChatClientContext(short version) {
        this(DEFAULT_CONTEXT_NAME, version);
    }

    public ChatClientContext() {
        this(DEFAULT_VERSION);
    }


    public boolean sendPack(NPack pack) {
        if (null == context) {
            return false;
        }
        context.writeAndFlush(pack);
        return true;
    }


    public boolean sendPack(NPack pack, GenericFutureListener<Future<? super Void>> listener) {
        if (null == context) {
            return false;
        }
        context.writeAndFlush(pack).addListener(listener);
        return true;
    }

    /**
     * 阻塞过程
     *
     * @return ChannelHandlerContext
     */
    public final ChannelHandlerContext getContext() {
        if (null == context) {
            while (true) {
                if (null != context) break;
            }
        }
        return context;
    }

    /**
     * 只允许开发者设置
     *
     * @param context ChannelHandlerContext
     */
    void setContext(ChannelHandlerContext context) {
        this.context = context;
    }


    /**
     * 客户端启动
     */
    protected void onStart() {
    }


    /**
     * 客户端关闭
     */
    protected void onFail() {
    }

    /**
     * 客户端接收到信息
     *
     * @param context NPack
     */
    protected abstract void onReading(NPack context);


    /**
     * 一个空对象
     */
    public static final ChatClientContext NULL = new ChatClientContext() {

        @Override
        protected void onReading(NPack context) {
            // do nothing
        }
    };
}
