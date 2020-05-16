package com.misc.core.netty;

import com.misc.core.exception.HandlerException;
import com.misc.core.model.MiscMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.Executor;

/**
 * 核心处理器
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@io.netty.channel.ChannelHandler.Sharable
public class ServerHandler extends ChannelDuplexHandler {
    private Executor executor;

    private ChannelHandler channelHandler;

    public ServerHandler(Executor executor, ChannelHandler channelHandler) {
        this.executor = executor;
        this.channelHandler = channelHandler;
    }

    @SuppressWarnings("all")
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        executor.execute(() -> {
            try {
                channelHandler.received(ctx.channel(), (MiscMessage) msg);
            } catch (HandlerException e) {
                channelHandler.caught(ctx.channel(), e);
            }
        });
    }

    @SuppressWarnings("all")
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            channelHandler.sent(ctx.channel(), (MiscMessage) msg);
        } finally {
            super.write(ctx, msg, promise);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            channelHandler.caught(ctx.channel(), cause);
        } finally {
            super.exceptionCaught(ctx, cause);
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            channelHandler.disconnected(ctx.channel());
        } finally {
            super.channelActive(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            channelHandler.connected(ctx.channel());
        } finally {
            super.channelInactive(ctx);
        }
    }
}
