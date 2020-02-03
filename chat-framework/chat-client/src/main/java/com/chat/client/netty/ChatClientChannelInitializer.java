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

/**
 * 添加 处理器
 */
public final class ChatClientChannelInitializer extends ChannelInitializer<Channel> {

    private final ChatEventListener listener;
    private final short version;


    ChatClientChannelInitializer(short version, ChatEventListener listener) {
        this.listener = listener;
        this.version = version;
    }


    @Override
    protected void initChannel(Channel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // in 解码器
        pipeline.addLast("decoder", new PackageDecoder(version));

        // out 编码器 , 最好放在第一个
        pipeline.addLast("encoder", new PackageEncoder(version));


        // 心跳检测 , 如果60S 我们收不到服务器发来的请求 , 我们就发送一个心跳包
        pipeline.addLast("nettyHeartBeatHandler", new IdleStateHandler(40, 0, 0));

        // 处理器
        pipeline.addLast("heartBeatHandler", new ClientHeartBeatHandler(listener));

        // in
        pipeline.addLast("handler", new ChantClientHandler(listener));
    }
}
