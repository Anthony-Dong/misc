package com.chat.client.netty;


import com.chat.client.hander.ChatClientContext;
import com.chat.client.hander.ClientChatHandlerConstant;
import com.chat.core.ServerNode;
import com.chat.core.exception.BootstrapException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventListener;
import com.chat.core.listener.ChatEventType;
import com.chat.core.netty.Constants;
import com.chat.core.netty.NettyProperties;
import com.chat.core.netty.PackageDecoder;
import com.chat.core.netty.PackageEncoder;
import com.chat.core.util.NetUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NetUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.chat.core.netty.PropertiesConstant.*;
import static com.chat.core.netty.Constants.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * chat client
 *
 * @date:2019/11/10 11:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class ChatClient extends ServerNode {
    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);
    /**
     * 用来阻塞
     */
    private final Object Lock = new Object();

    /**
     * 地址
     */
    private final InetSocketAddress address;

    /**
     * 监听器
     */
    private final ChatEventListener listener;

    /**
     * 定义属性
     */
    private final NettyProperties properties;

    /**
     * 默认事件循环组
     */
    private static final NioEventLoopGroup workerGroup = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("AsyncChatClientWorker", true));

    /**
     * @param listener listener  事件监听器
     */
    private ChatClient(NettyProperties properties, ChatEventListener listener, Executor executor) {
        this.properties = properties;
        this.address = getAddress(properties);
        this.listener = listener;
        this.executor = executor;
    }

    private static InetSocketAddress getAddress(NettyProperties properties) {
        return new InetSocketAddress(properties.getString(CLIENT_HOST, DEFAULT_HOST), properties.getInt(CLIENT_PORT, DEFAULT_PORT));
    }

    private final Executor executor;

    /**
     * 启动
     *
     * @throws Exception sync() 异常往外抛出
     */
    @Override
    protected void start() throws Exception {
        // 初始化一堆属性
        final Bootstrap bootstrap = new Bootstrap();
        final ChantClientHandler handler = new ChantClientHandler(listener, executor, address);
        final short version = properties.getShort(CLIENT_PROTOCOL_VERSION, PROTOCOL_VERSION);
        final byte type = properties.getByte(CLIENT_PROTOCOL_TYPE, DEFAULT_SERIALIZABLE_TYPE.getCode());
        final String dir = properties.getString(CLIENT_FILE_DIR, DEFAULT_FILE_DIR);
        final int heartInterval = properties.getInt(CLIENT_HEART_INTERVAL, DEFAULT_CLIENT_HEART_INTERVAL);
        final long timeout = properties.getLong(CLIENT_TIME_OUT, DEFAULT_CONNECT_TIMEOUT);

        // 设置属性
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                // 这几个参数参考自 org.apache.dubbo.remoting.transport.netty4.NettyClient.
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout < 3000 ? 3000 : Math.toIntExact(timeout))
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // in 解码器
                        pipeline.addLast("decoder", new PackageDecoder(version, dir));
                        // out 编码器 , 最好放在第一个
                        pipeline.addLast("encoder", new PackageEncoder(version, type));
                        // 心跳检测 , 如果60S 我们收不到服务器发来的请求 , 我们就发送一个心跳包
                        pipeline.addLast("nettyHeartBeatHandler", new IdleStateHandler(heartInterval, 0, 0));
                        // 处理器
                        //pipeline.addLast("heartBeatHandler", new ClientHeartBeatHandler(listener, address));
                        // in
                        pipeline.addLast("handler", handler);
                    }
                });


        ChannelFuture future = bootstrap.connect(address).sync();
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
        future.awaitUninterruptibly(timeout, MILLISECONDS);
    }


    /**
     * 出现异常,直接关闭
     */
    @Override
    public void shutDown() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        synchronized (Lock) {
            Lock.notifyAll();
        }
        logger.debug("[客户端] Shutdown success the connected server host: {}, port: {}.", NetUtils.filterLocalHost(address.getHostName()), address.getPort());
    }


    /**
     * 启动项
     *
     * @param context 上下文
     * @throws BootstrapException
     */
    public static ChatClient run(ChatClientContext context, long timeout) throws BootstrapException {
        //2. 初始化事件
        ClientChatHandlerConstant constant = new ClientChatHandlerConstant(context);
        Map<ChatEventType, ChatEventHandler> handlerMap = constant.getHandlerMap();

        ChatClient client = new ChatClient(context.getProperties(), event -> {
            ChatEventHandler handler = handlerMap.get(event.eventType());
            handler.handler(event);
        }, context.getThreadPool().getExecutor());

        try {
            client.start();
        } catch (Exception e) {
            client.shutDown();
            throw new RuntimeException(e.getMessage());
        }

        // 等待启动
        try {
            // true表示计数器为0 ,启动成功. 这样做的原因是为了防止后期出现异常 , 要拿到channelContext对象
            boolean await = context.getLatch().await(timeout, MILLISECONDS);
            if (!await) {
                throw new BootstrapException("等待超时,关闭与服务器连接");
            }
        } catch (Exception e) {
            client.shutDown();
            throw new BootstrapException(e.getMessage());
        }

        // 为了后期关闭做准备.
        context.setClient(client);
        return client;
    }

    /**
     * 默认超时时间为3000MS
     *
     * @throws BootstrapException
     */
    public static ChatClient run(int port, ChatClientContext context) throws BootstrapException {
        context.setPort(port);
        return run(context, Constants.DEFAULT_CONNECT_TIMEOUT);
    }


    public static ChatClient run(String host, int port, ChatClientContext context) throws BootstrapException {
        context.setHostName(host);
        context.setPort(port);
        return run(context, Constants.DEFAULT_CONNECT_TIMEOUT);
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
                this.shutDown();
            }
        }
    }

    /**
     * 写法上弥补
     */
    public void close() {
        shutDown();
    }
}