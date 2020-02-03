package com.chat.client.netty;


import com.chat.client.hander.ChatClientContext;
import com.chat.client.hander.ClientChatHandlerConstant;
import com.chat.core.ServerNode;
import com.chat.core.annotation.NotNull;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventListener;
import com.chat.core.listener.ChatEventType;
import com.chat.core.util.NamedThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * chat client
 *
 * @date:2019/11/10 11:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class AsyncChatClient extends ServerNode {

    private static final Logger logger = LoggerFactory.getLogger(AsyncChatClient.class);

    // 创建一个事件循环组
    private final EventLoopGroup workerGroup;

    // IP
    private final InetSocketAddress address;

    // 添加启动监听器
    private final ChatEventListener listener;

    // 版本号
    private final short version;

    /**
     * @param workerGroup workerGroup   线程组
     * @param address     address   服务器地址
     * @param listener    listener  事件监听器
     */
    private AsyncChatClient(NioEventLoopGroup workerGroup, short version, InetSocketAddress address, ChatEventListener listener) {
        this.version = version;
        this.workerGroup = workerGroup;
        this.address = address;
        this.listener = listener;
    }


    /**
     * 启动
     *
     * @throws Exception sync() 异常往外抛出
     */
    @Override
    protected void start() throws Exception {

        logger.info("[客户端] 开始启动 Host : {}  Port : {} .", this.address.getHostName(), this.address.getPort());

        final Bootstrap bootstrap = new Bootstrap();
        final ChatClientChannelInitializer initializer = new ChatClientChannelInitializer(version, listener);

        // 设置属性
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                // 这几个参数参考自 org.apache.dubbo.remoting.transport.netty4.NettyClient.
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(initializer);

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
        future.awaitUninterruptibly(3000, MILLISECONDS);
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
    protected void shutDown() throws Exception {
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

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }


    /**
     * 启动项
     *
     * @param workerThread 工作线程  默认一个就行
     * @param address      服务器地址
     * @param context      上下文
     * @throws Exception
     */
    public static AsyncChatClient run(int workerThread, InetSocketAddress address, @NotNull ChatClientContext context) throws Exception {
        ClientChatHandlerConstant constant = new ClientChatHandlerConstant(context);

        String name = context.getContextName();

        Map<ChatEventType, ChatEventHandler> handlerMap = constant.getHandlerMap();

        AsyncChatClient client = new AsyncChatClient(new NioEventLoopGroup(workerThread, new NamedThreadFactory(name)), context.getVersion(), address, event -> {
            ChatEventHandler handler = handlerMap.get(event.eventType());
            handler.handler(event);
        });
        client.start();
        return client;
    }

    public static AsyncChatClient run(int port, int workerThread, @NotNull ChatClientContext context) throws Exception {
        return run(workerThread, new InetSocketAddress(port), context);
    }

    public static AsyncChatClient run(InetSocketAddress address, @NotNull ChatClientContext context) throws Exception {
        return run(1, address, context);
    }

    public static AsyncChatClient run(int port, @NotNull ChatClientContext context) throws Exception {
        return run(new InetSocketAddress(port), context);
    }
}
