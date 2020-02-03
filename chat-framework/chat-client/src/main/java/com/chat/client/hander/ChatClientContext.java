package com.chat.client.hander;

import com.chat.core.exception.ContextException;
import com.chat.core.model.NPack;
import com.chat.core.netty.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.function.Consumer;

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
        return this.contextName;
    }


    public final short getVersion() {
        return this.version;
    }


    public ChatClientContext(String contextName, int version) {
        if (version > Short.MAX_VALUE || version < Short.MIN_VALUE) {
            throw new RuntimeException("Version range in -32768 with 32767 !");
        }

        this.contextName = contextName;
        this.version = (short) version;
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

    public boolean sendPack(NPack pack) throws ContextException {
        return sendPack(pack, null, null);
    }

    public boolean sendPack(NPack pack, Consumer<NPack> consumer) throws ContextException {
        return sendPack(pack, consumer, null);
    }


    public boolean sendPack(NPack pack, Consumer<NPack> consumer, GenericFutureListener<Future<? super Void>> listener) {
        if (context == null) {
            throw new ContextException("初始化未完成,无法发送消息");
        }
        if (consumer != null) {
            consumer.accept(pack);
        }
        if (listener == null) {
            context.writeAndFlush(pack);
        } else {
            context.writeAndFlush(pack).addListener(listener);
        }
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
    protected void onReading(NPack context) {

    }

    /**
     * 一个空对象
     */
    public static ChatClientContext newInstance() {
        return new ChatClientContext() {
            @Override
            protected void onReading(NPack context) {
                System.out.println(Thread.currentThread().getName() + " : " + context);
            }
        };
    }
}
