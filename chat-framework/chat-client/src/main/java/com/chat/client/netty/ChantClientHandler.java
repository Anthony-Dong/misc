package com.chat.client.netty;

import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventListener;
import com.chat.core.listener.ChatEventType;
import com.chat.core.model.NPack;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;

/**
 * 适配器 -- > 主要的业务逻辑
 */
public class ChantClientHandler extends SimpleChannelInboundHandler<NPack> {

    private ChatEventListener listener;

    ChantClientHandler(ChatEventListener listener) {
        this.listener = listener;
    }

    /**
     * 如果是 IO 异常直接关闭 交给 {@link ChantClientHandler#handlerRemoved} 处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NPack msg) throws Exception {
        listener.onChatEvent(new ChatEvent() {
            @Override
            public ChatEventType eventType() {
                return ChatEventType.CLIENT_READ;
            }

            @Override
            public Object event() {
                return msg;
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        listener.onChatEvent(new ChatEvent() {
            @Override
            public ChatEventType eventType() {
                return ChatEventType.CLIENT_CONNECTED;
            }

            @Override
            public Object event() {
                return ctx;
            }
        });
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }
}