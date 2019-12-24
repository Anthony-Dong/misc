package com.chat.client.netty;


import com.chat.core.ServerNode;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventListener;
import com.chat.core.listener.ChatEventType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * chat client
 *
 * @date:2019/11/10 11:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ChatClient implements ServerNode {

    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);

    // 创建一个事件循环组
    private final EventLoopGroup workerGroup;

    // IP
    private final InetSocketAddress address;

    // 添加启动监听器
    private final ChatEventListener listener;


    /**
     * @param workerGroup workerGroup   线程组
     * @param address     address   服务器地址
     * @param listener    listener  事件监听器
     */
    public ChatClient(NioEventLoopGroup workerGroup, InetSocketAddress address, ChatEventListener listener) {
        this.workerGroup = workerGroup;
        this.address = address;
        this.listener = listener;
    }


    /**
     * 构造方法
     *
     * @param address  address   服务器地址
     * @param listener listener  事件监听器
     */
    public ChatClient(InetSocketAddress address, ChatEventListener listener) {
        this(new NioEventLoopGroup(1), address, listener);
    }

    /**
     * 启动
     *
     * @throws Exception sync() 异常往外抛出
     */
    @Override
    public void start() throws Exception {

        logger.info("[客户端] 开始启动 Host : {}  Port : {} .", this.address.getHostName(), this.address.getPort());

        final Bootstrap bootstrap = new Bootstrap();

        // 设置属性
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChatClientChannelInitializer(listener));


        try {

            ChannelFuture channelFuture = bootstrap.connect(address).sync();

            // 发送事件
            listener.onChatEvent(new ChatEvent() {
                @Override
                public ChatEventType eventType() {
                    return ChatEventType.CLIENT_START;
                }

                @Override
                public Object event() {
                    return address;
                }
            });


            // 阻塞执行线程
            channelFuture.channel().closeFuture().sync();
        } finally {

            // 关闭
            listener.onChatEvent(new ChatEvent() {
                @Override
                public ChatEventType eventType() {
                    return ChatEventType.CLIENT_SHUTDOWN;
                }

                @Override
                public Object event() {
                    return address;
                }
            });
            shutDown();
        }
    }

    /**
     * 当出现异常可以直接关闭
     */
    @Override
    public void shutDown() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
