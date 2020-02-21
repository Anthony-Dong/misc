package com.chat.server.netty;


import java.net.InetSocketAddress;
import java.util.Map;

import com.chat.core.ServerNode;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventListener;
import com.chat.core.listener.ChatEventType;
import com.chat.core.netty.Constants;
import com.chat.core.netty.FileAndPackageDecoder;
import com.chat.core.netty.PackageDecoder;
import com.chat.core.netty.PackageEncoder;
import com.chat.core.util.ThreadPool;
import com.chat.server.handler.ChatServerContext;
import com.chat.server.handler.ServerChatHandlerConstant;
import com.chat.server.handler.ServerStartChatEventHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;


/**
 * 服务器端
 */
public class ChatServer extends ServerNode {
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
    private final ChatEventListener listener;

    private final short version;

    /**
     * 线程池
     */
    private final ThreadPool threadPool;

    private final boolean openFileProtocol;


    /**
     * 获取线程池状态
     */
    public ThreadPool getThreadPool() {
        return threadPool;
    }

    /**
     * 构造方法 , 初始化一堆参数
     *
     * @param threadPool 真正处理事务的线程池
     * @param address    地址
     * @param listener   监听器
     */
    public ChatServer(short version, InetSocketAddress address, ChatEventListener listener, ThreadPool threadPool, boolean openFileProtocol) {
        this.threadPool = threadPool;
        this.version = version;
        this.address = address;
        this.listener = listener;
        this.openFileProtocol = openFileProtocol;
        this.bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyServerBoss", true));
        this.workerGroup = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("NettyServerWorker", true));
    }

    /**
     * 服务器 启动脚本
     *
     * @throws Exception 服务器异常关闭
     */
    @Override
    protected void start() throws Exception {
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
//        final ChatServerInitializer initializer = new ChatServerInitializer(listener, version, threadPool);
        final ChatServerHandler handler = new ChatServerHandler(listener, threadPool);
        serverBootstrap
                .group(bossGroup, workerGroup) // 添加组
                .channel(NioServerSocketChannel.class)  // 添加管道
                // 这几个参数考自 org.apache.dubbo.remoting.transport.netty4.NettyServer
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // out  编码器
                        pipeline.addLast("encoder", new PackageEncoder(version));

                        if (openFileProtocol) {
                            // 文件解码器+NPack解码器
                            pipeline.addLast("file-decoder", new FileAndPackageDecoder(version));
                        } else {
                            // Npack解码器
                            pipeline.addLast("decoder", new PackageDecoder(version));
                        }
                        // 心跳检测
                        pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, 90));

                        // 心跳检测处理器
                        pipeline.addLast("serverHeartBeatHandler", new ChatServerHeartBeatHandler());

                        // handler
                        pipeline.addLast("handler", handler);
                    }
                });//设置初始化项目

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
     * @param context 上下文
     * @throws Exception
     */
    public static void run(ChatServerContext context) throws Exception {
        if (context.getAddress() == null) {
            context.setAddress(new InetSocketAddress(Constants.DEFAULT_HOST, Constants.DEFAULT_PORT));
        }
        if (context.getThreadPool() == null) {
            context.setThreadPool(new ThreadPool(Constants.DEFAULT_THREAD_SIZE, Constants.DEFAULT_QUEUE_SIZE, Constants.DEFAULT_THREAD_NAME));
        }

        final ServerChatHandlerConstant constant = new ServerChatHandlerConstant(context);

        final Map<ChatEventType, ChatEventHandler> handlerMap = constant.getHandlerMap();

        //启动
        final ChatServer server = new ChatServer(context.getVersion(), context.getAddress(), event -> {
            ChatEventHandler handler = handlerMap.get(event.eventType());
            handler.handler(event);
        }, context.getThreadPool(), context.isUseFileProtocol());
        server.start();
    }

    public static void run(int port, ChatServerContext context) throws Exception {
        context.setAddress(new InetSocketAddress(Constants.DEFAULT_HOST, port));
        run(context);
    }
}
