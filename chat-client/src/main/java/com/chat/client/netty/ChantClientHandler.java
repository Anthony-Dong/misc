package com.chat.client.netty;

import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventListener;
import com.chat.core.listener.ChatEventType;
import com.chat.core.model.NPack;
import com.chat.core.model.URL;
import com.chat.core.model.UrlConstants;
import com.chat.core.util.NetUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

/**
 * 适配器 -- > 主要的业务逻辑
 */
@ChannelHandler.Sharable
public final class ChantClientHandler extends SimpleChannelInboundHandler<NPack> {
    private static final Logger logger = LoggerFactory.getLogger(ChantClientHandler.class);

    private final Executor executor;

    private ChatEventListener listener;

    private final NPack pack;

    ChantClientHandler(ChatEventListener listener, Executor executor, InetSocketAddress address) {
        URL url = new URL(UrlConstants.HEART_PROTOCOL, NetUtils.filterLocalHost(address.getHostName()), address.getPort());
        this.pack = new NPack(URL.decode(url.toString()));
        this.listener = listener;
        this.executor = executor;
    }

    /**
     * 如果是 IO 异常直接关闭 交给 {@link ChantClientHandler#handlerRemoved} 处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            logger.error("[客户端] 发生异常 服务器 IP : {}  Exception : {}.", ctx.channel().remoteAddress().toString(), cause.getMessage());
            ctx.close();
        } finally {
            ctx.fireExceptionCaught(cause);
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

    /**
     * 当连接建立起来了
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
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
        } finally {
            ctx.fireChannelActive();
        }
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            // 直接发送关闭
            listener.onChatEvent(new ChatEvent() {
                @Override
                public Object event() {
                    return ctx.channel().remoteAddress();
                }

                @Override
                public ChatEventType eventType() {
                    return ChatEventType.CLIENT_SHUTDOWN;
                }


            });
        } finally {
            super.handlerRemoved(ctx);
        }
    }


    /**
     * 处理心跳,不需要release,所以不需要执行fire操作
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 从下往上遍历
            ctx.writeAndFlush(pack.update());
        }
    }
}