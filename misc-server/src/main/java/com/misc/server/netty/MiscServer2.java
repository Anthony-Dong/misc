package com.misc.server.netty;


import com.misc.core.AbstractMiscNode;
import com.misc.core.commons.Constants;
import com.misc.core.context.AbstractServerContext;
import com.misc.core.env.MiscProperties;
import com.misc.core.exception.BootstrapException;
import com.misc.core.func.FunctionType;
import com.misc.core.func.FunctionTypeAdapter;
import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEvent;
import com.misc.core.listener.MiscEventListener;
import com.misc.core.listener.MiscEventType;
import com.misc.core.netty.ChannelHandler;
import com.misc.core.netty.ProtocolAdapter;
import com.misc.core.netty.ProtocolHandler;
import com.misc.core.netty.ServerHandler;
import com.misc.core.netty.rpc.RpcServerChannelHandler;
import com.misc.core.proto.ProtocolType;
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

import java.util.Map;
import java.util.concurrent.Executor;

import static com.misc.core.commons.Constants.DEFAULT_SERVER_HEART_INTERVAL;
import static com.misc.core.commons.PropertiesConstant.SERVER_HEART_INTERVAL;


/**
 * 服务器端
 */
public class MiscServer2 extends AbstractMiscNode {
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
    public MiscServer2(MiscProperties properties,
                        Executor executor,
                        ProtocolType protocolType,
                        FunctionType functionType,
                        ChannelHandler channelHandler
    ) {
        super(null, executor, properties, protocolType, functionType, channelHandler);
    }

    /**
     * 服务器 启动脚本
     *
     * @throws Exception 服务器异常关闭
     */
    @Override
    public void start() throws Exception {
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        final ServerHandler handler = new ServerHandler(executor, channelHandler);
        final ProtocolAdapter protocolCodecAdapter = new ProtocolAdapter();
        final FunctionTypeAdapter functionTypeAdapter = new FunctionTypeAdapter();
        functionTypeAdapter.getProtocolHandler(protocolType, functionType);
        final ProtocolHandler protocolHandler = null;

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
                        pipeline.addLast(protocolCodecAdapter.getHandler(protocolType, properties, serializeHandlerMap));

                        pipeline.addLast(protocolHandler);
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


}
