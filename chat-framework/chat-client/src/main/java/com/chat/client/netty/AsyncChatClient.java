package com.chat.client.netty;


import com.chat.client.hander.ChatClientContext;
import com.chat.client.hander.ClientChatHandlerConstant;
import com.chat.core.ServerNode;
import com.chat.core.annotation.NotNull;
import com.chat.core.exception.BootstrapException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventListener;
import com.chat.core.listener.ChatEventType;
import com.chat.core.netty.Constants;
import com.chat.core.netty.PackageDecoder;
import com.chat.core.netty.PackageEncoder;
import com.chat.core.util.NamedThreadFactory;
import com.chat.core.util.ThreadPool;
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
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * chat client
 *
 * @date:2019/11/10 11:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class AsyncChatClient extends ServerNode {

    private static final Logger logger = LoggerFactory.getLogger(AsyncChatClient.class);

    /**
     * 地址
     */
    private final InetSocketAddress address;

    /**
     * 监听器
     */
    private final ChatEventListener listener;

    /**
     * 版本号
     */
    private final short version;

    private final long timeout;

    private final int heartInterval;

    /**
     * 默认事件循环组
     */
    //Constants.DEFAULT_IO_THREADS
    private static final NioEventLoopGroup workerGroup = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("AsyncChatClientWorker", true));

    /**
     * @param address  address   服务器地址
     * @param listener listener  事件监听器
     */
    private AsyncChatClient(short version, InetSocketAddress address, ChatEventListener listener, Executor executor, long timeout,int heartInterval) {
        this.version = version;
        this.address = address;
        this.listener = listener;
        this.executor = executor;
        this.timeout = timeout;
        this.heartInterval = heartInterval;
    }

    private Executor executor;

    /**
     * 启动
     *
     * @throws Exception sync() 异常往外抛出
     */
    @Override
    protected void start() throws Exception {
        logger.debug("[客户端] 开始启动 Host : {}  Port : {} .", this.address.getHostName(), this.address.getPort());
        final Bootstrap bootstrap = new Bootstrap();
        final ChantClientHandler handler = new ChantClientHandler(listener, executor);
        // 设置属性
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                // 这几个参数参考自 org.apache.dubbo.remoting.transport.netty4.NettyClient.
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // in 解码器
                        pipeline.addLast("decoder", new PackageDecoder(version));
                        // out 编码器 , 最好放在第一个
                        pipeline.addLast("encoder", new PackageEncoder(version));
                        // 心跳检测 , 如果60S 我们收不到服务器发来的请求 , 我们就发送一个心跳包
                        pipeline.addLast("nettyHeartBeatHandler", new IdleStateHandler(heartInterval, 0, 0));
                        // 处理器
                        pipeline.addLast("heartBeatHandler", new ClientHeartBeatHandler(listener, address));
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
     * 符合编程规范
     */
    public void close() {
        try {
            shutDown();
        } catch (Exception e) {
            //
        }
    }

    /**
     * 当出现异常可以直接关闭
     */
    @Override
    protected void shutDown() {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }


    /**
     * 启动项 ,
     *
     * @param context 上下文
     * @throws BootstrapException
     */
    public static AsyncChatClient run(ChatClientContext context, long timeout) throws BootstrapException {
        //1. 初始化上下文
        ChatClientContext.init(context);

        //2. 初始化事件
        ClientChatHandlerConstant constant = new ClientChatHandlerConstant(context);
        Map<ChatEventType, ChatEventHandler> handlerMap = constant.getHandlerMap();

        AsyncChatClient client = new AsyncChatClient(context.getVersion(), context.getAddress(), event -> {
            ChatEventHandler handler = handlerMap.get(event.eventType());
            handler.handler(event);
        }, context.getThreadPool().getExecutor(), timeout, context.getHeartInterval());
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

        return client;
    }

    /**
     * 默认超时时间为3000MS
     *
     * @throws BootstrapException
     */
    public static AsyncChatClient run(int port, ChatClientContext context) throws BootstrapException {
        context.setAddress(new InetSocketAddress(Constants.DEFAULT_HOST, port));
        return run(context, Constants.DEFAULT_TIMEOUT);
    }
}