package com.chat.client.core;

import com.chat.core.listener.ChatBootListener;
import com.chat.core.packutil.PackageDecoder;
import com.chat.core.packutil.PackageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 添加 处理器
 */
public class ChatClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ChatBootListener listener;

    private NioEventLoopGroup workerGroup;

    public ChatClientChannelInitializer(ChatBootListener listener, NioEventLoopGroup workerGroup) {
        this.listener = listener;
        this.workerGroup = workerGroup;
    }


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // out 编码器
        pipeline.addLast("encoder", new PackageEncoder());

        // 心跳检测 , 如果60S 我们收不到服务器发来的请求 , 我们就发送一个心跳包
        pipeline.addLast("nettyHeartBeatHandler", new IdleStateHandler(30, 0, 0));

        // 处理器
        pipeline.addLast("heartBeatHandler", new ClientHeartBeatHandler(listener));

        // in 解码器
        pipeline.addLast("decoder", new PackageDecoder());

        // in
        pipeline.addLast("handler", new ChantClientHandler(listener, workerGroup));
    }
}
