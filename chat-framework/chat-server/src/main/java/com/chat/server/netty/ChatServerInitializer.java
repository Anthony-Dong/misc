package com.chat.server.netty;

import com.chat.core.listener.ChatEventListener;
import com.chat.core.netty.PackageDecoder;
import com.chat.core.netty.PackageEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * ChannelInitializer 初始化器
 */
public final class ChatServerInitializer extends ChannelInitializer<Channel> {

    private final ChatServerHandler handler;

    private final short version;

    ChatServerInitializer(ChatEventListener listener, short version) {
        handler = new ChatServerHandler(listener);
        this.version = version;
    }

    @Override
    protected void initChannel(Channel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();

        // out  编码器
        pipeline.addLast("encoder", new PackageEncoder(version));

        // 心跳检测
        pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, 90));

        // 心跳检测处理器
        pipeline.addLast("serverHeartBeatHandler", new ChatServerHeartBeatHandler());

        // 解码器
        pipeline.addLast("decoder", new PackageDecoder(version));

        // 后置处理器
        pipeline.addLast("handler", handler);
    }

}