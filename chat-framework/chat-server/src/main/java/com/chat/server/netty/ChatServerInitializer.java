package com.chat.server.netty;

import com.chat.core.listener.ChatEventListener;
import com.chat.core.netty.PackageDecoder;
import com.chat.core.netty.PackageEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * ChannelInitializer
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {

    private final ChatServerHandler handler;

    ChatServerInitializer(ChatEventListener listener) {
        handler = new ChatServerHandler(listener);
    }

    @Override
    protected void initChannel(Channel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // out  编码器
        pipeline.addLast("encoder", new PackageEncoder());

        // 心跳检测
        pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, 120));

        // 心跳检测处理器
        pipeline.addLast("serverHeartBeatHandler", new ChatServerHeartBeatHandler());

        // 解码器
        pipeline.addLast("decoder", new PackageDecoder());

        // 后置处理器
        pipeline.addLast("handler", handler);
    }

}