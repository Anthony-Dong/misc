package com.misc.core.netty;


import com.misc.core.commons.Constants;
import com.misc.core.util.ThreadPool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static com.misc.core.commons.Constants.*;

/**
 * @param <ProtoInBound>    协议的入站口
 * @param <ProtoOutBound>   协议的出站口
 * @param <ChannelInBound>  处理的数据
 * @param <ChannelOutBound> 响应的数据
 *                          关系         ProtoInBound -> ChannelInBound
 *                          ChannelOutBound -> ProtoOutBound
 *                          <p>
 *                          codecProvider 是提供编解码器将   ProtoInBound 和 ProtoOutBound 解码
 */
public final class NettyServer<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> implements NettyNode {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    /**
     * boss 组 一般是 一个
     */
    private EventLoopGroup bossGroup;
    /**
     * work 组 ,一般是CPU 的个数
     */
    private EventLoopGroup workerGroup;
    /**
     * 线程池
     */
    private ThreadPool threadPool;
    /**
     * 心跳
     */
    private int heartInterval;

    /**
     * 远程地址
     */
    private SocketAddress address;


    /**
     * 编解码器（不一定共享根据需求）
     */
    private NettyCodecProvider<ProtoInBound, ProtoOutBound> codecProvider;
    /**
     * 协议转换器（共享，单例）
     */
    private NettyConvertHandler<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> nettyConvertHandler;


    /**
     * 事件处理器（共享）
     */
    private NettyEventListener<ChannelInBound, ChannelOutBound> nettyEventListener;

    /**
     * channel
     */
    private volatile Channel channel;
    private volatile boolean init = false;

    /**
     * 不允许外部实例化，全部采用build
     */
    private NettyServer() {

    }

    /**
     * 服务器 启动脚本
     *
     * @throws Exception 服务器异常关闭
     */
    @Override
    public synchronized NettyNode start() throws Throwable {
        if (init) {
            logger.warn("Netty-Server[{}] already started", address);
            return this;
        }
        final ServerBootstrap serverBootstrap = new ServerBootstrap();

        final ServerHandler<ChannelInBound, ChannelOutBound> serverHandler = new ServerHandler<>(threadPool.getExecutor(), nettyEventListener);
        serverBootstrap
                .group(bossGroup, workerGroup) // 添加组
                .channel(NioServerSocketChannel.class)  // 添加管道
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 空检测
                        if (codecProvider != null) {
                            ChannelHandler[] codecs = codecProvider.get();
                            if (codecs != null && codecs.length > 0) {
                                // 核心编解码器
                                pipeline.addLast(codecs);
                            }
                        }
                        if (nettyConvertHandler != null) {
                            // 业务编解码器
                            pipeline.addLast(nettyConvertHandler);
                        }

                        // 心跳检测
                        pipeline.addLast(new IdleStateHandler(0, 0, heartInterval));

                        // 真正的处理器
                        pipeline.addLast(serverHandler);
                    }
                });//设置初始化项目

        try {
            ChannelFuture channelFuture = serverBootstrap.bind(address);
            channelFuture.syncUninterruptibly();
            this.channel = channelFuture.channel();
            logger.info("Netty-Server[{}] start success", address);
            init = true;
            return this;
        } catch (Throwable e) {
            nettyEventListener.caught(channel, e);
            close();
            throw e;
        }
    }


    /**
     * 同步
     */
    @Override
    public synchronized NettyNode sync() throws Throwable {
        try {
            this.channel.closeFuture().sync();
        } catch (InterruptedException e) {
            nettyEventListener.caught(this.channel, e);
            throw e;
        } finally {
            close();
        }
        return this;
    }

    /**
     * 关闭
     */
    @Override
    public NettyNode close() throws Throwable {
        if (channel != null) {
            channel.close();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("Netty-Server[{}] close success", address);
        return this;
    }


    /**
     * 构造器模式
     */
    public static <ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> Builder<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> builder() {
        return new Builder<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound>() {
            @Override
            protected void init() {

            }
        };
    }

    /**
     * 设置builder
     *
     * @param <ProtoInBound>
     * @param <ProtoOutBound>
     * @param <ChannelInBound>
     * @param <ChannelOutBound>
     */
    public static abstract class Builder<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> {
        private final NettyServer<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> server;

        /**
         * 优先级小于address
         */
        private String host;
        private int port;


        public Builder() {
            this.server = new NettyServer<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound>();
            init();
        }

        public Builder setHost(String host) {
            if (server.address != null) {
                logger.warn("address {} already set !", server.address);
            }
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            if (server.address != null) {
                logger.warn("address {} already set !", server.address);
            }
            this.port = port;
            return this;
        }

        public Builder setAddress(SocketAddress address) {
            server.address = address;
            return this;
        }

        public Builder setBossGroup(EventLoopGroup bossGroup) {
            server.bossGroup = bossGroup;
            return this;
        }

        public Builder setWorkerGroup(EventLoopGroup workerGroup) {
            server.workerGroup = workerGroup;
            return this;
        }


        public Builder setThreadPool(ThreadPool threadPool) {
            server.threadPool = threadPool;
            return this;
        }


        public Builder setHeartInterval(int heartInterval) {
            server.heartInterval = heartInterval;
            return this;
        }

        public Builder setNettyCodecProvider(NettyCodecProvider<ProtoInBound, ProtoOutBound> codecProvider) {
            server.codecProvider = codecProvider;
            return this;
        }


        public Builder setNettyConvertHandler(NettyConvertHandler<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> nettyConvertHandler) {
            server.nettyConvertHandler = nettyConvertHandler;
            return this;
        }

        public Builder setNettyEventListener(NettyEventListener<ChannelInBound, ChannelOutBound> nettyEventListener) {
            server.nettyEventListener = nettyEventListener;
            return this;
        }

        public NettyServer<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> build() {
            check();
            return server;
        }

        private void check() {
            if (server.nettyEventListener == null) {
                throw new NullPointerException("nettyEventListener 不允许为空");
            }
            if (server.nettyConvertHandler == null) {
                logger.warn("The nettyConvertHandler is null , please careful !");
            }

            if (server.codecProvider == null) {
                logger.warn("The codecProvider is null , please careful !");
            }
            server.bossGroup = server.bossGroup == null ? new NioEventLoopGroup(1, new DefaultThreadFactory("MiscServerBoss", true)) : server.bossGroup;
            server.workerGroup = server.workerGroup == null ? new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("NettyServerWorker", true)) : server.workerGroup;
            server.threadPool = server.threadPool == null ? new ThreadPool(DEFAULT_THREAD_SIZE, DEFAULT_THREAD_QUEUE_SIZE, DEFAULT_THREAD_NAME) : server.threadPool;
            server.heartInterval = server.heartInterval < 30 ? DEFAULT_SERVER_HEART_INTERVAL : server.heartInterval;
            this.host = host == null || host.length() == 0 ? "localhost" : this.host;
            this.port = this.port == 0 ? 9999 : this.port;
            server.address = server.address == null ? new InetSocketAddress(host, port) : server.address;

            logger.info("Netty-Server[{}] init the config is {} {} {} {} {}",
                    server.address,
                    server.bossGroup,
                    server.workerGroup,
                    server.threadPool,
                    server.heartInterval,
                    server.nettyEventListener
            );
        }

        /**
         * 初始化使用
         */
        protected void init() {

        }
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public int getHeartInterval() {
        return heartInterval;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public NettyCodecProvider<ProtoInBound, ProtoOutBound> getCodecProvider() {
        return codecProvider;
    }

    public NettyConvertHandler<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> getNettyConvertHandler() {
        return nettyConvertHandler;
    }

    public NettyEventListener<ChannelInBound, ChannelOutBound> getNettyEventListener() {
        return nettyEventListener;
    }

    public Channel getChannel() {
        return channel;
    }
}
