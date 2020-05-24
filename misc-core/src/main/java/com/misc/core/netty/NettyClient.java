package com.misc.core.netty;


import com.misc.core.exception.BootstrapException;
import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.core.util.NetUtils;
import com.misc.core.util.StringUtils;
import com.misc.core.util.ThreadPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;

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
public final class NettyClient<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> implements NettyNode {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    /**
     * boss 组
     */
    private EventLoopGroup bossGroup;

    /**
     * 线程池,主要是用来处理拿到响应结果的，
     */
    private ThreadPool threadPool;

    /**
     * 心跳 ， 单位s
     */
    private int heartInterval;

    /**
     * 连接超时时间 , 单位ms
     */
    private int connectTimeout;

    /**
     * 远程地址
     */
    private SocketAddress address;

    /**
     * 编解码器（不一定共享，根据需求）
     */
    private NettyCodecProvider<ProtoInBound, ProtoOutBound> codecProvider;

    /**
     * 协议转换器（共享，单例）
     */
    private NettyConvertHandler<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> nettyConvertHandler;

    /**
     * 事件处理器（共享，单例）
     */
    private NettyEventListener<ChannelInBound, ChannelOutBound> nettyEventListener;


    /**
     * channel
     */
    private volatile Channel channel;

    /**
     * 防止重复初始化
     */
    private volatile boolean init = false;

    /**
     *
     */
    private CountDownLatch channelLock = new CountDownLatch(1);

    /**
     * 不允许外部实例化，全部采用build
     */
    private NettyClient() {
    }

    /**
     * 客户端 启动脚本
     *
     * @throws BootstrapException 启动
     */
    @Override
    public synchronized NettyClient<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> start() throws Throwable {
        if (init) {
            logger.warn("Netty-Client[{}] already started", address);
            return this;
        }
        final Bootstrap bootstrap = new Bootstrap();

        /**
         * 这个主要是因为客户端不需要线程池
         */
        final ServerHandler<ChannelInBound, ChannelOutBound> serverHandler = new ServerHandler<>(threadPool == null ? null : threadPool.getExecutor(), nettyEventListener);

        bootstrap
                .group(bossGroup) // 添加组
                .channel(NioSocketChannel.class)  // 添加管道
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout < 3000 ? 3000 : Math.toIntExact(connectTimeout))
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        if (codecProvider != null) {
                            ChannelHandler[] codecs = codecProvider.get();
                            if (codecs != null && codecs.length > 0) {
                                // 核心编解码器
                                pipeline.addLast(codecs);
                            }
                        }
                        // 业务编解码器
                        if (nettyConvertHandler != null) {
                            pipeline.addLast(nettyConvertHandler);
                        }
                        // 心跳检测
                        pipeline.addLast(new IdleStateHandler(heartInterval, 0, 0));

                        // 真正的处理器
                        pipeline.addLast(serverHandler);
                    }
                });//设置初始化项目
        try {
            this.channel = bootstrap.connect(address).sync().channel();
            channelLock.countDown();
            logger.info("Netty-Client[{}] start success", address);
            init = true;
            return this;
        } catch (Throwable e) {
            nettyEventListener.caught(channel, e);
            close();
            throw e;
        }
    }

    /**
     * 等待关闭
     */
    @Override
    public synchronized NettyClient<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> sync() throws Throwable {
        try {
            this.channel.closeFuture().sync();
        } catch (InterruptedException e) {
            nettyEventListener.caught(channel, e);
            throw e;
        } finally {
            close();
        }
        return this;
    }

    /**
     * 正确关闭和释放资源
     */
    @Override
    public synchronized NettyClient<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> close() throws Throwable {
        if (channel != null) {
            channel.close();
        }
        bossGroup.shutdownGracefully();
        nettyEventListener.disconnected(channel);
        logger.info("Netty-Client[{}] close success", address);
        // 防止异常
        channelLock.countDown();
        return this;
    }

    /**
     * 构造器模式
     */
    public static <ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> Builder<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> builder() {
        return new Builder<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound>() {
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
        private final NettyClient<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> server;

        private String host;
        private int port;
        private int threadPoolSize;
        private int threadQueueSize;
        private String threadName;

        public Builder() {
            this.server = new NettyClient<>();
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setThreadPoolSize(int threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public Builder setThreadQueueSize(int threadQueueSize) {
            this.threadQueueSize = threadQueueSize;
            return this;
        }

        public Builder setThreadName(String threadName) {
            this.threadName = threadName;
            return this;
        }

        public Builder setBossGroup(EventLoopGroup bossGroup) {
            server.bossGroup = bossGroup;
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

        public Builder setConnectTimeout(int connectTimeout) {
            server.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * 主要是继承这个
         */
        public NettyClient<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> build() {
            check();
            return server;
        }

        private void check() {
            init();
            if (server.nettyEventListener == null) {
                throw new NullPointerException("nettyEventListener 不允许为空");
            }

            if (server.nettyConvertHandler == null) {
                logger.warn("The nettyConvertHandler is null , please careful !");
            }

            if (server.codecProvider == null) {
                logger.warn("The codecProvider is null , please careful !");
            }
            server.address = NetUtils.getInetSocketAddress(this.host, this.port);
            server.bossGroup = server.bossGroup == null ? new NioEventLoopGroup(1, new DefaultThreadFactory(NetUtils.formatAddr((InetSocketAddress) server.address), true)) : server.bossGroup;
            server.heartInterval = server.heartInterval < 30 ? DEFAULT_CLIENT_HEART_INTERVAL : server.heartInterval;
            if (threadPoolSize != 0) {
                server.threadPool = new ThreadPool(this.threadPoolSize, this.threadQueueSize == 0 ? DEFAULT_THREAD_QUEUE_SIZE : threadQueueSize, StringUtils.isEmpty(this.threadName) ? NetUtils.formatAddr((InetSocketAddress) server.address) : this.threadName);
            }
            logger.info("{} init the config is  {} {} {}",
                    NetUtils.formatAddr((InetSocketAddress) server.address),
                    server.address,
                    server.bossGroup,
                    server.heartInterval,
                    server.nettyEventListener
            );
        }

        protected void init() {

        }
    }


    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public int getHeartInterval() {
        return heartInterval;
    }

    public int getConnectTimeout() {
        return connectTimeout;
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
        try {
            // 防止没有获取到channel
            channelLock.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Server init failed");
        }
        return channel;
    }
}
