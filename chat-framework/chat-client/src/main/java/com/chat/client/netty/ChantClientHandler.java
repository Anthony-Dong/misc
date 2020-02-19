package com.chat.client.netty;

import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventListener;
import com.chat.core.listener.ChatEventType;
import com.chat.core.model.NPack;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * 适配器 -- > 主要的业务逻辑
 */
@ChannelHandler.Sharable
public final class ChantClientHandler extends SimpleChannelInboundHandler<NPack> {
    private static final Logger logger = LoggerFactory.getLogger(ChantClientHandler.class);

    private final Executor executor;

    private ChatEventListener listener;

    ChantClientHandler(ChatEventListener listener, Executor executor) {
        this.listener = listener;
        this.executor = executor;
    }

    /**
     * 如果是 IO 异常直接关闭 交给 {@link ChantClientHandler#handlerRemoved} 处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            ctx.close();
        } else {
            logger.error("[客户端] 发生异常 服务器 IP : {}  Exception : {}.", ctx.channel().remoteAddress().toString(), cause.getMessage());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NPack msg) throws Exception {
        // 由于害怕解码线程阻塞.所以加入了找个线程池
        executor.execute(() -> {
            try {
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
            } catch (Exception e) {
                throw new RuntimeException(e);
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