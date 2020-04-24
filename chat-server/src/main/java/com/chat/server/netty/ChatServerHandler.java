package com.chat.server.netty;

import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventListener;
import com.chat.core.listener.ChatEventType;
import com.chat.core.model.NPack;
import com.chat.core.util.ThreadPool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.Executor;

/**
 * 服务器 通用处理器
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<NPack> {

    private static final Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);

    private final ChatEventListener listener;

    private final Executor executor;

    @Override
    public boolean isSharable() {
        return true;
    }

    ChatServerHandler(ChatEventListener listener, ThreadPool threadPool) {
        super(true);
        this.listener = listener;
        this.executor = threadPool.getExecutor();
    }


    /**
     * 不需要传递,浪费时间罢了
     *
     * @param evt 心跳事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            logger.error("[服务器] 心跳服务 IP:{}为客户端即将断开连接,心跳超时.", ctx.channel().remoteAddress());
            ctx.close();
        }
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
    }


    /**
     * 客户端连接成功
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            ctx.fireChannelActive();
        } finally {
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
    }

    /**
     * 如果发生异常就关闭
     *
     * @throws Exception 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error("[服务器] Happened exception the client-ip: {} will be disconnected because of :{}.", ctx.channel().remoteAddress(), cause.getMessage());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NPack msg) throws Exception {
        executor.execute(() -> {
            try {
                listener.onChatEvent(new ChatEvent() {
                    @Override
                    public ChatEventType eventType() {
                        return ChatEventType.SERVER_READ;
                    }
                    @Override
                    public Object event() {
                        SocketAddress address = ctx.channel().remoteAddress();
                        msg.setAddress(address);
                        return msg;
                    }
                });
            } catch (Exception e) {
                logger.error("[服务器] Happened exception client-ip: {}, exception: {}.", ctx.channel().remoteAddress(), e.getMessage());
            } finally {
                msg.release();
            }
        });
    }
}
