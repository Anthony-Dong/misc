package com.chat.client.netty;

import com.chat.core.listener.ChatEventListener;
import com.chat.core.netty.PackageDecoder;
import com.chat.core.netty.PackageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 添加 处理器
 */
public class ChatClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ChatEventListener listener;


    public ChatClientChannelInitializer(ChatEventListener listener) {
        this.listener = listener;
    }


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // out 编码器
        pipeline.addLast("encoder", new PackageEncoder());

        // 心跳检测 , 如果60S 我们收不到服务器发来的请求 , 我们就发送一个心跳包
        pipeline.addLast("nettyHeartBeatHandler", new IdleStateHandler(5, 0, 0));

        // 处理器
        pipeline.addLast("heartBeatHandler", new ClientHeartBeatHandler(listener));


        // in 解码器
        pipeline.addLast("decoder", new PackageDecoder());

        // in
        pipeline.addLast("handler", new ChantClientHandler(listener));
    }
}
