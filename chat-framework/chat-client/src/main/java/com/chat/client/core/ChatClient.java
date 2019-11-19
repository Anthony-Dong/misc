package com.chat.client.core;


import com.chat.core.ServerNode;
import com.chat.core.exception.ConnectionException;
import com.chat.core.listener.ChatBootEvent;
import com.chat.core.listener.ChatBootListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * chat client
 */
public class ChatClient implements ServerNode {

    private static Logger logger = LoggerFactory.getLogger(ChatClient.class);

    // 创建一个事件循环组
    private NioEventLoopGroup workerGroup;


    // 启动IP 之类的
    private InetSocketAddress address;


    // 主要的传输对象 -- > 需要拿到一个future
    private ChannelFuture channelFuture;


    // 添加启动监听器
    private ChatBootListener listener;


    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }


    /**
     * 一般使用这个  -> 因为我们可以执行关闭线程组,通过监听器
     * @param workerGroup
     * @param address
     * @param listener
     */
    public ChatClient(NioEventLoopGroup workerGroup, InetSocketAddress address,  ChatBootListener listener) {
        this.workerGroup = workerGroup;
        this.address = address;
        this.listener = listener;
    }

    /**
     * 如果是 springboot , 可以做初始化操作 ,
     * 其他情况使用建议 用 构造方法初始化
     */
    @Override
    public void init() throws Exception {
        // TODO: 2019/11/14  可以自定义启动项,我用的其他方法
    }

    /**
     * 启动脚本
     *
     * @throws Exception 异常往外抛出
     */
    @Override
    public void start() throws Exception {
        logger.info("[客户端-{}:{}] 开始启动", this.address.getHostName(), this.address.getPort());
        Bootstrap bootstrap = new Bootstrap();

        // 设置属性
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChatClientChannelInitializer(listener, workerGroup));
        // 连接
        this.channelFuture = connect(this.address, bootstrap);

    }


    /**
     * 主要的连接流程
     *
     * 有些人会问为什么不执行 connect().sync() 方法, 那是因为你不知道sync方法的含义是什么 ,它是使得当前的主线程就是开启客户端的线程阻塞,直到异常/关闭
     *
     * closeFuture().sync()也是同样的阻塞 ,所以我们要分情况而定
     *
     * @param remoteAddress
     * @param bootstrap
     * @return
     */
    public ChannelFuture connect(SocketAddress remoteAddress, Bootstrap bootstrap) throws Exception {
        return bootstrap.connect(remoteAddress).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    // 成功
                    logger.info("[客户端-{}:{}] 启动成功", address.getHostName(), address.getPort());
                    listener.onChatBootEvent(new ChatBootEvent(ChatBootEvent.CLIENT_SUCCESS));
                } else {
                    listener.onChatBootEvent(new ChatBootEvent(ChatBootEvent.CLIENT_FAILURE));
                    throw new ConnectionException("[客户端-" + address.getHostName() + ":" + address.getPort() + "] 启动失败", future.cause());
                }
            }
        });
    }


    /**
     * 当出现异常可以直接关闭
     */
    @Override
    public void shutDown() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
