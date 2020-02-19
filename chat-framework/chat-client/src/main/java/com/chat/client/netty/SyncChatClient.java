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
import com.chat.core.netty.PackageDecoder;
import com.chat.core.netty.PackageEncoder;
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
import java.util.concurrent.Executors;

/**
 * chat client
 *
 * @date:2019/11/10 11:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Deprecated
public final class SyncChatClient extends ServerNode {

    private static final Logger logger = LoggerFactory.getLogger(SyncChatClient.class);

    /**
     * 默认事件循环组
     */
    private static final NioEventLoopGroup workerGroup = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("AsyncChatClientWorker", true));

    // IP
    private final InetSocketAddress address;

    // 添加启动监听器
    private final ChatEventListener listener;

    /**
     * 版本号, C-S端必须一致, 不然解码错误
     */
    private final short version;

    /**
     * 心跳
     */
    private final int heartInterval;

    /**
     * @param version  b版本号
     * @param address  address   服务器地址
     * @param listener listener  事件监听器
     */
    public SyncChatClient(short version, InetSocketAddress address, ChatEventListener listener, int heartInterval) {
        this.version = version;
        this.address = address;
        this.listener = listener;
        this.heartInterval = heartInterval;
    }

    /**
     * 启动
     *
     * @throws Exception sync() 异常往外抛出
     */
    @Override
    protected void start() throws Exception {
        logger.debug("[客户端] 开始启动 Host : {}  Port : {} .", this.address.getHostName(), this.address.getPort());

        try {
            final Bootstrap bootstrap = new Bootstrap();
            final ChantClientHandler handler = new ChantClientHandler(listener, Executors.newFixedThreadPool(1));

            // 设置属性
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    // 这几个参数参考自 org.apache.dubbo.remoting.transport.netty4.NettyClient
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


            final ChannelFuture sync = bootstrap.connect(address).sync();
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
            sync.channel().closeFuture().sync();
        } finally {
            shutDown();
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
     * @param context 上下文
     * @throws BootstrapException
     */
    public static void run(ChatClientContext context) throws BootstrapException {
        ChatClientContext.init(context);
        ClientChatHandlerConstant constant = new ClientChatHandlerConstant(context);
        Map<ChatEventType, ChatEventHandler> handlerMap = constant.getHandlerMap();
        SyncChatClient client = new SyncChatClient(context.getVersion(), context.getAddress(), event -> {
            ChatEventHandler handler = handlerMap.get(event.eventType());
            handler.handler(event);
        }, context.getHeartInterval());
        try {
            client.start();
        } catch (Exception e) {
            throw new BootstrapException(e);
        }
    }


    public static void run(int port, ChatClientContext context) throws BootstrapException {
        context.setAddress(new InetSocketAddress(Constants.DEFAULT_HOST, port));
        run(context);
    }
}
