package com.misc.client.netty;

import com.misc.core.listener.MiscEvent;
import com.misc.core.listener.MiscEventListener;
import com.misc.core.listener.MiscEventType;
import com.misc.core.model.MiscPack;
import com.misc.core.model.URL;
import com.misc.core.model.UrlConstants;
import com.misc.core.util.NetUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

/**
 * 适配器 -- > 主要的业务逻辑
 */
@ChannelHandler.Sharable
public final class ChantClientHandler extends SimpleChannelInboundHandler<MiscPack> {
    private static final Logger logger = LoggerFactory.getLogger(ChantClientHandler.class);

    private final Executor executor;

    private MiscEventListener listener;

    private final MiscPack pack;

    ChantClientHandler(MiscEventListener listener, Executor executor, InetSocketAddress address) {
        URL url = new URL(UrlConstants.HEART_PROTOCOL, NetUtils.filterLocalHost(address.getHostName()), address.getPort());
        this.pack = new MiscPack(URL.decode(url.toString()));
        this.listener = listener;
        this.executor = executor;
    }

    /**
     * 如果是 IO 异常直接关闭 交给 {@link ChantClientHandler#handlerRemoved} 处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            logger.error("[Misc-Client] Happened exception the remote address: {} will be disconnected because of :{}.", ctx.channel().remoteAddress(), cause.getMessage());
            ctx.close();
        } finally {
            ctx.fireExceptionCaught(cause);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MiscPack msg) throws Exception {
        // 由于害怕解码线程阻塞.所以加入了找个线程池
        executor.execute(() -> {
            try {
                listener.onChatEvent(new MiscEvent() {
                    @Override
                    public MiscEventType eventType() {
                        return MiscEventType.CLIENT_READ;
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
            listener.onChatEvent(new MiscEvent() {
                @Override
                public MiscEventType eventType() {
                    return MiscEventType.CLIENT_CONNECTED;
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
            listener.onChatEvent(new MiscEvent() {
                @Override
                public Object event() {
                    return ctx.channel().remoteAddress();
                }

                @Override
                public MiscEventType eventType() {
                    return MiscEventType.CLIENT_SHUTDOWN;
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
            // 更新时间，不需要考虑多线程并发问题
            ctx.writeAndFlush(pack.update());
        }
    }
}