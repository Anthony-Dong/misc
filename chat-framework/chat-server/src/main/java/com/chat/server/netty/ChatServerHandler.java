package com.chat.server.netty;

import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventListener;
import com.chat.core.listener.ChatEventType;
import com.chat.core.model.NPack;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 服务器 通用处理器
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<NPack> {

    private static final Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);

    private ChatEventListener listener;

    @Override
    public boolean isSharable() {
        return true;
    }

    ChatServerHandler(ChatEventListener listener) {
        this.listener = listener;
    }


    /**
     * 执行断开业务的逻辑
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        listener.onChatEvent(new ChatEvent() {
            @Override
            public ChatEventType eventType() {
                return ChatEventType.SERVER_HANDLER_REMOVED;
            }

            @Override
            public Object event() {
                return ctx;
            }
        });
        super.handlerRemoved(ctx);
    }

    /**
     * 注册成功 像客户端发送一个响应
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        listener.onChatEvent(new ChatEvent() {
            @Override
            public ChatEventType eventType() {
                return ChatEventType.SERVER_CHANNEL_REGISTERED;
            }

            @Override
            public Object event() {
                return ctx;
            }
        });
    }

    /**
     * 如果发生异常就关闭
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
            logger.error("[服务器] 发生异常 客户端 IP : {}  Exception : {}.", ctx.channel().remoteAddress(), cause.getMessage());
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NPack msg) throws Exception {

        listener.onChatEvent(new ChatEvent() {
            @Override
            public ChatEventType eventType() {
                return ChatEventType.SERVER_READ;
            }

            @Override
            public Object event() {
                return msg;
            }
        });
    }
}
