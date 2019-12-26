package com.chat.client.hander;

import com.chat.core.model.NPack;
import io.netty.channel.ChannelHandlerContext;

public abstract class ChatClientContext {

    private volatile ChannelHandlerContext context;

    public final String getContextName() {
        return contextName;
    }


    public ChatClientContext() {
    }

    public ChatClientContext(String contextName) {
        this.contextName = contextName;
    }

    private String contextName;

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

    // 启动上下文
    protected abstract void onStart();

    // 关闭上下文
    protected abstract void onFail();

    // 读
    protected abstract void onReading(NPack context);

    public static final ChatClientContext NULL = new ChatClientContext() {
        @Override
        protected void onStart() {
        }

        @Override
        protected void onFail() {
        }

        @Override
        protected void onReading(NPack context) {
        }
    };
}
