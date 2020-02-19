package com.chat.client.netty;

import com.chat.core.listener.ChatEventListener;
import com.chat.core.netty.PackageDecoder;
import com.chat.core.netty.PackageEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

/**
 * 添加 处理器
 */
public final class ChatClientChannelInitializer extends ChannelInitializer<Channel> {

    private final ChatEventListener listener;
    private final short version;
    private final InetSocketAddress address;
    private final Executor executor;


    ChatClientChannelInitializer(short version, ChatEventListener listener, InetSocketAddress address,Executor executor) {
        this.listener = listener;
        this.version = version;
        this.address = address;
        this.executor = executor;
    }


    @Override
    protected void initChannel(Channel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // in 解码器
        pipeline.addLast("decoder", new PackageDecoder(version));

        // out 编码器 , 最好放在第一个
        pipeline.addLast("encoder", new PackageEncoder(version));

        // 心跳检测 , 如果60S 我们收不到服务器发来的请求 , 我们就发送一个心跳包
        pipeline.addLast("nettyHeartBeatHandler", new IdleStateHandler(5, 0, 0));

        // 处理器
        pipeline.addLast("heartBeatHandler", new ClientHeartBeatHandler(listener,address));

        // in
        pipeline.addLast("handler", new ChantClientHandler(listener,executor));
    }
}
