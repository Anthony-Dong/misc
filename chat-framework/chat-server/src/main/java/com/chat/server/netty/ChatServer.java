package com.chat.server.netty;


import java.net.InetSocketAddress;
import java.util.Map;

import com.chat.core.ServerNode;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventListener;
import com.chat.core.listener.ChatEventType;
import com.chat.core.util.NamedThreadFactory;
import com.chat.server.handler.ChatServerContext;
import com.chat.server.handler.ServerChatHandlerConstant;
import com.chat.server.handler.ServerStartChatEventHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器端
 */

public class ChatServer extends ServerNode {

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);
    /**
     * boss 组 一般是 一个
     */
    private final EventLoopGroup bossGroup;
    /**
     * work 组 ,一般是CPU 的个数
     */
    private final EventLoopGroup workerGroup;
    /**
     * netty 绑定的 ip
     */
    private final InetSocketAddress address;

    /**
     * 启动监听器
     * 可以看 {@link ChatEvent SERVER_SUCCESS} 属性去判断 失败/成功
     */
    private ChatEventListener listener;

    private short version;

    /**
     * 构造方法 , 初始化一堆参数
     *
     * @param address
     * @param listener
     */
    public ChatServer(short version, InetSocketAddress address, ChatEventListener listener) {
        this.version = version;
        this.address = address;
        this.listener = listener;
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new NamedThreadFactory("chat-server"));
    }

    /**
     * 服务器 启动脚本
     *
     * @throws Exception 服务器异常关闭
     */
    @Override
    protected void start() throws Exception {
        logger.info("[服务器] 开始启动 Host : {}  Port : {}.", address.getHostName(), address.getPort());
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        final ChatServerInitializer initializer = new ChatServerInitializer(listener, version);
        serverBootstrap
                .group(bossGroup, workerGroup) // 添加组
                .channel(NioServerSocketChannel.class)  // 添加管道
                .option(ChannelOption.SO_BACKLOG, 1024)  // 设置TCP连接数队列
                .childHandler(initializer)//设置初始化项目
                .childOption(ChannelOption.TCP_NODELAY, true);

        try {
            final ChannelFuture channelFuture = serverBootstrap.bind(address).sync();
            /**
             * {@link ServerStartChatEventHandler}  处理器
             */
            listener.onChatEvent(new ChatEvent() {
                @Override
                public ChatEventType eventType() {
                    return ChatEventType.SERVER_START;
                }

                @Override
                public Object event() {
                    return address;
                }
            });

            // 当前启动线程到这里阻塞中
            channelFuture.channel().closeFuture().sync();
        } finally {

            /**
             * {@link com.chat.server.handler.ServerShutdownChatEventHandler}  处理器
             */
            listener.onChatEvent(new ChatEvent() {
                @Override
                public ChatEventType eventType() {
                    return ChatEventType.SERVER_SHUTDOWN;
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
     * 关闭两个线程组 ,当失败的时候 ,
     *
     * @throws Exception 异常
     */
    @Override
    protected void shutDown() throws Exception {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }


    /**
     * 这是个阻塞方法, 只有发生异常, 才会停止
     *
     * @param address 服务器地址
     * @param context 上下文
     * @throws Exception
     */
    public static void run(InetSocketAddress address, ChatServerContext context) throws Exception {
        final ServerChatHandlerConstant constant = new ServerChatHandlerConstant(context);

        final Map<ChatEventType, ChatEventHandler> handlerMap = constant.SERVER_MAP;

        final ChatServer server = new ChatServer(context.getVersion(), address, event -> {
            ChatEventHandler handler = handlerMap.get(event.eventType());
            handler.handler(event);
        });

        server.start();
    }

    public static void run(int port, ChatServerContext context) throws Exception {
        run(new InetSocketAddress(port), context);
    }

}
