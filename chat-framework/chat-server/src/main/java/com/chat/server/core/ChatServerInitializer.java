package com.chat.server.core;

import com.chat.core.listener.ChatBootListener;
import com.chat.core.packutil.PackageDecoder;
import com.chat.core.packutil.PackageEncoder;
import com.chat.server.util.RedisPool;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ChannelInitializer
 */
public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {

    private AtomicInteger totalConnection;

    private RedisPool redisPool;

    private InetSocketAddress address;

    private ChatBootListener listener;

    public ChatServerInitializer(AtomicInteger totalConnection, RedisPool redisPool, ChatBootListener listener, InetSocketAddress address) {
        this.totalConnection = totalConnection;
        this.redisPool = redisPool;
        this.listener = listener;
        this.address = address;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // out  编码器
        pipeline.addLast("encoder", new PackageEncoder());

        // 心跳检测
        pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, 60));

        // 心跳检测处理器
        pipeline.addLast("serverHeartBeatHandler", new ChatServerHeartBeatHandler(listener));

        // 解码器
        pipeline.addLast("decoder", new PackageDecoder());

        // 后置处理器
        pipeline.addLast("handler", new ChatServerHandler(redisPool, totalConnection, listener,address));
    }

}