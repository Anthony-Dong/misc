package com.chat.client.core;

import com.chat.core.listener.ChatBootEvent;
import com.chat.core.listener.ChatBootListener;
import com.chat.core.listener.ChatBootSource;
import com.chat.core.model.NPack;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 适配器 -- > 主要的业务逻辑
 */
public class ChantClientHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ChantClientHandler.class);

    private NioEventLoopGroup workerGroup;

    private ChatBootListener listener;

    public ChantClientHandler(ChatBootListener listener, NioEventLoopGroup workerGroup) {
        this.listener = listener;
        this.workerGroup = workerGroup;
    }

    /**
     * 如果是 IO 异常直接关闭 交给 {@link ChantClientHandler#handlerRemoved} 处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("[客户端] 发生异常 : {}",cause.getMessage() );
        if (cause instanceof IOException) {
            // 先关闭连接诶 - > handlerRemoved()
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof NPack) {
            NPack messages = (NPack) msg;
            logger.info("[客户端] 接收信息 : {}", messages);
        }
    }

    /**
     * 执行移除 handler  发布一个事件 CLIENT_SHUTDOWN 的事件
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        listener.onChatBootEvent(new ChatBootEvent(ChatBootEvent.CLIENT_SHUTDOWN));
    }
}