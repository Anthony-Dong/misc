package com.misc.core.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * 核心处理器
 * 为啥要泛型呢， 是因为为了高度的拓展
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@io.netty.channel.ChannelHandler.Sharable
public class ServerHandler<INBOUND, OUTBOUND> extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    /**
     * 线程池
     */
    private Executor executor;

    /**
     * 处理器
     */
    private NettyEventListener<INBOUND, OUTBOUND> nettyEventListener;

    /**
     * 构造器
     */
    ServerHandler(Executor executor, NettyEventListener<INBOUND, OUTBOUND> nettyEventListener) {
        this.executor = executor;
        this.nettyEventListener = nettyEventListener;
    }


    /**
     * 接收 ， 不向下传递
     */
    @SuppressWarnings("all")
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 如果我们没有线程池（客户端一般没有）
            if (executor == null) {
                logger.debug("[MISC] received {}", msg);
                nettyEventListener.received(ctx.channel(), (INBOUND) msg);
            } else {
                // 有
                executor.execute(() -> {
                    try {
                        logger.debug("[MISC] received {}", msg);
                        nettyEventListener.received(ctx.channel(), (INBOUND) msg);
                    } catch (Throwable e) {
                        // 发送异常传递
                        nettyEventListener.caught(ctx.channel(), e);
                    }
                });
            }
        } catch (Throwable e) {
            // 异常
            nettyEventListener.caught(ctx.channel(), e);
        }
    }


    /**
     * 写出 ，向上传递
     */
    @SuppressWarnings("all")
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            logger.debug("[MISC] sent {}", msg);
            nettyEventListener.sent(ctx.channel(), (OUTBOUND) msg);
        } finally {
            super.write(ctx, msg, promise);
        }
    }

    /**
     * 异常 , 不向下传递 ,不然会大量报错
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("[MISC] caught address: {}, exception:{}", ctx.channel().remoteAddress(), cause.getMessage());
        nettyEventListener.caught(ctx.channel(), cause);
    }


    /**
     * 连接 ，向下传递
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            logger.debug("[MISC] connected {}", ctx.channel().remoteAddress());
            nettyEventListener.connected(ctx.channel());
        } finally {
            super.channelActive(ctx);
        }
    }

    /**
     * 移除 ，向下传递
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            logger.debug("[MISC] disconnected {}", ctx.channel().remoteAddress());
            nettyEventListener.disconnected(ctx.channel());
        } finally {
            super.channelInactive(ctx);
        }
    }

    /**
     * 事件触发转发，不向下传递
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.debug("[MISC] event triggered {}", evt);
        nettyEventListener.eventTriggered(ctx.channel(), evt);
    }
}
