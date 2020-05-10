package com.misc.client.netty;


import com.misc.client.hander.MiscClientContext;
import com.misc.client.hander.ClientChatHandlerConstant;
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
import com.misc.core.util.NetUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executor;

import static com.misc.core.commons.PropertiesConstant.*;
import static com.misc.core.commons.Constants.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * misc client
 *
 * @date:2019/11/10 11:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class MiscClient extends AbstractMiscNode {

    private static final Logger logger = LoggerFactory.getLogger(MiscClient.class);

    /**
     * 阻塞的工具
     */
    private final Object Lock = new Object();

    /**
     * 默认事件循环组
     */
    private static final NioEventLoopGroup workerGroup = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("MiscClient", true));


    /**
     * @param listener listener  事件监听器
     */
    private MiscClient(MiscProperties properties, MiscEventListener listener, Executor executor, ProtocolType protocolType) {
        super(getAddress(properties), listener, executor, properties, protocolType);
    }

    /**
     * 启动
     *
     * @throws Exception sync() 异常往外抛出
     */
    @Override
    public void start() throws Exception {
        // 初始化属性
        final Bootstrap bootstrap = new Bootstrap();
        final ChantClientHandler handler = new ChantClientHandler(listener, executor, address);
        final int heartInterval = properties.getInt(CLIENT_HEART_INTERVAL, DEFAULT_CLIENT_HEART_INTERVAL);
        final long connctTimeout = properties.getLong(CLIENT_CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
        final ProtocolAdapter protocolAdapter = new ProtocolAdapter();


        // 设置属性
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                // 这几个参数参考自 org.apache.dubbo.remoting.transport.netty4.NettyClient.
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connctTimeout < 3000 ? 3000 : Math.toIntExact(connctTimeout))
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 协议处理器
                        pipeline.addLast(protocolAdapter.getHandler(protocolType, properties, serializeHandlerMap));
                        // 心跳检测 , 如果60S 我们收不到服务器发来的请求 , 我们就发送一个心跳包
                        pipeline.addLast("nettyHeartBeatHandler", new IdleStateHandler(heartInterval, 0, 0));
                        // 处理器
                        pipeline.addLast("handler", handler);
                    }
                });


        ChannelFuture future = bootstrap.connect(address).sync();
        // 发送事件
        listener.onChatEvent(new MiscEvent() {
            @Override
            public MiscEventType eventType() {
                return MiscEventType.CLIENT_START;
            }

            @Override
            public Object event() {
                return address;
            }
        });
        future.awaitUninterruptibly(connctTimeout, MILLISECONDS);
    }

    /**
     * 出现异常,直接关闭
     */
    @Override
    public void stop() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        synchronized (Lock) {
            Lock.notifyAll();
        }
        logger.debug("[Misc-Client] Shutdown success the connected server host: {}, port: {}.", NetUtils.filterLocalHost(address.getHostName()), address.getPort());

    }

    /**
     * 启动项
     *
     * @param context 上下文
     * @throws BootstrapException
     */
    public static MiscClient run(MiscClientContext context) throws BootstrapException {
        //2. 初始化事件
        ClientChatHandlerConstant constant = new ClientChatHandlerConstant(context);
        Map<MiscEventType, MiscEventHandler> handlerMap = constant.getHandlerMap();

        MiscClient client = new MiscClient(context, event -> {

            // 主要的处理逻辑
            MiscEventHandler handler = handlerMap.get(event.eventType());
            handler.handler(event);

        }, context.getThreadPool().getExecutor(), context.getProtocolType());

        try {
            client.start();
        } catch (Exception e) {
            client.stop();
            throw new BootstrapException(String.format("[Misc-Client] Bootstrap timeout: %dms", context.getConnectTimeout()));
        }

        // 等待启动
        try {
            // true表示计数器为0 ,启动成功. 这样做的原因是为了防止后期出现异常 , 要拿到channelContext对象
            boolean await = context.getLatch().await(context.getConnectTimeout(), MILLISECONDS);
            if (!await) {
                throw new BootstrapException(String.format("[Misc-Client] Bootstrap timeout: %dms", context.getConnectTimeout()));
            }
        } catch (Exception e) {
            client.stop();
            throw new BootstrapException(e.getMessage());
        }
        // 后期监听关闭
        context.setClient(client);
        return client;
    }

    /**
     * 默认超时时间为3000MS
     *
     * @throws BootstrapException
     */
    public static MiscClient run(int port, MiscClientContext context) throws BootstrapException {
        context.setPort(port);
        return run(context);
    }


    public static MiscClient run(String host, int port, MiscClientContext context) throws BootstrapException {
        context.setHost(host);
        context.setPort(port);
        return run(context);
    }

    /**
     * 等到服务器被关闭.
     */
    public void sync() {
        synchronized (Lock) {
            try {
                Lock.wait();
            } catch (InterruptedException e) {
                // 错误了直接关闭服务器
                this.stop();
            }
        }
    }

    /**
     * 写法上弥补
     */
    public void close() {
        stop();
    }
}