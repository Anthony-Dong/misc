package com.chat.server.netty;

import com.chat.core.listener.ChatEventListener;
import com.chat.core.netty.PackageDecoder;
import com.chat.core.netty.PackageEncoder;
import com.chat.core.util.ThreadPool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * ChannelInitializer 初始化器
 */
@Deprecated
public final class ChatServerInitializer extends ChannelInitializer<Channel> {

    private final ChatServerHandler handler;

    private final short version;

    ChatServerInitializer(ChatEventListener listener, short version, ThreadPool threadPool) {
        handler = new ChatServerHandler(listener, threadPool);
        this.version = version;
    }

    @Override
    protected void initChannel(Channel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();

        // 解码器
        pipeline.addLast("decoder", new PackageDecoder(version));

        // out  编码器
        pipeline.addLast("encoder", new PackageEncoder(version));

        // 心跳检测
        pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, 90));

        // 心跳检测处理器
        pipeline.addLast("serverHeartBeatHandler", new ChatServerHeartBeatHandler());

        // handler
        pipeline.addLast("handler", handler);
    }

}