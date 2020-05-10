package com.misc.server.netty;


import java.util.Map;
import java.util.concurrent.Executor;

import com.misc.core.AbstractMiscNode;
import com.misc.core.exception.BootstrapException;
import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEvent;
import com.misc.core.listener.MiscEventListener;
import com.misc.core.listener.MiscEventType;
import com.misc.core.commons.Constants;
import com.misc.core.env.MiscProperties;
import com.misc.core.proto.ProtocolAdapter;
import com.misc.core.proto.ProtocolType;
import com.misc.core.proto.misc.MiscCodecHandler;
import com.misc.server.handler.MiscServerContext;
import com.misc.server.handler.ServerChatHandlerConstant;
import com.misc.server.handler.ServerShutdownMiscEventHandler;
import com.misc.server.handler.ServerStartMiscEventHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import static com.misc.core.commons.Constants.*;
import static com.misc.core.commons.PropertiesConstant.*;


/**
 * 服务器端
 */
public class MiscServer extends AbstractMiscNode {
    /**
     * boss 组 一般是 一个
     */
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("MiscServerBoss", true));
    ;
    /**
     * work 组 ,一般是CPU 的个数
     */
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("NettyServerWorker", true));

    /**
     * 构造方法 , 初始化一堆参数
     */
    private MiscServer(MiscProperties properties, MiscEventListener listener, Executor executor, ProtocolType protocolType) throws BootstrapException {
        super(getAddress(properties), listener, executor, properties, protocolType);
    }

    /**
     * 服务器 启动脚本
     *
     * @throws Exception 服务器异常关闭
     */
    @Override
    public void start() throws Exception {
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        final MiscServerHandler handler = new MiscServerHandler(listener, executor);
        final ProtocolAdapter protocolAdapter = new ProtocolAdapter();
        final int heartInterval = properties.getInt(SERVER_HEART_INTERVAL, DEFAULT_SERVER_HEART_INTERVAL);

        serverBootstrap
                .group(bossGroup, workerGroup) // 添加组
                .channel(NioServerSocketChannel.class)  // 添加管道
                // 这几个参数考自 org.apache.dubbo.remoting.transport.netty4.NettyServer
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // out  编码器
                        pipeline.addLast(protocolAdapter.getHandler(protocolType, properties, serializeHandlerMap));
                        // 心跳检测
                        pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, heartInterval));
                        // handler
                        pipeline.addLast("handler", handler);
                    }
                });//设置初始化项目

        try {
            final ChannelFuture channelFuture = serverBootstrap.bind(address).sync();
            /**
             * {@link ServerStartMiscEventHandler}  处理器
             */
            listener.onChatEvent(new MiscEvent() {
                @Override
                public MiscEventType eventType() {
                    return MiscEventType.SERVER_START;
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
             * {@link ServerShutdownMiscEventHandler}  处理器
             */
            listener.onChatEvent(new MiscEvent() {
                @Override
                public MiscEventType eventType() {
                    return MiscEventType.SERVER_SHUTDOWN;
                }

                @Override
                public Object event() {
                    return address;
                }
            });
            stop();
        }
    }

    /**
     * 关闭两个线程组 ,当失败的时候 ,
     *
     * @throws Exception 异常
     */
    @Override
    public void stop() throws Exception {
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
    public static void run(MiscServerContext context) throws Exception {

        final ServerChatHandlerConstant constant = new ServerChatHandlerConstant(context);

        final Map<MiscEventType, MiscEventHandler> handlerMap = constant.getHandlerMap();

        //启动
        final MiscServer server = new MiscServer(context, event -> {
            MiscEventHandler handler = handlerMap.get(event.eventType());
            handler.handler(event);
        }, context.getThreadPool().getExecutor(), context.getProtocolType());
        server.start();
    }

    public static void run(int port, MiscServerContext context) throws Exception {
        context.setPort(port);
        run(context);
    }

    public static void run(String host, int port, MiscServerContext context) throws Exception {
        context.setPort(port);
        context.setHost(host);
        run(context);
    }
}
