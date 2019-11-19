package com.chat.server.core;


import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;


import com.chat.core.ServerNode;
import com.chat.core.exception.ConnectionException;
import com.chat.core.listener.ChatBootEvent;
import com.chat.core.listener.ChatBootListener;
import com.chat.server.util.RedisPool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器端
 */

public class ChatServer implements ServerNode {

    private static Logger logger = LoggerFactory.getLogger(ChatServer.class);

    /**
     * boss 组 一般是 一个
     */
    private EventLoopGroup bossGroup;


    /**
     * work 组 ,一般是CPU 的个数
     */
    private EventLoopGroup workerGroup;


    /**
     * netty 绑定的 ip
     */
    private InetSocketAddress address;


    /**
     * redis 连接池
     */
    private RedisPool redisPool;


    /**
     * 一个server 一个 计数器 ,统计客户端连接数
     */
    private AtomicInteger totalConnection;


    public AtomicInteger getTotalConnection() {
        return this.totalConnection;
    }

    /**
     * 启动监听器
     * 可以看 {@link ChatBootEvent SERVER_SUCCESS} 属性去判断 失败/成功
     */
    private ChatBootListener listener;

    /**
     * 构造方法 , 初始化一堆参数
     *
     * @param address
     * @param redisPool
     * @param listener
     */

    public ChatServer(InetSocketAddress address, RedisPool redisPool, AtomicInteger totalConnection, ChatBootListener listener) {
        this.address = address;
        this.redisPool = redisPool;
        this.totalConnection = totalConnection;
        this.listener = listener;
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
    }

    /**
     * 这个主要目的是为了 spring-boot启动
     */
    @Override
    public void init() {

        // TODO: 2019/11/14  啥也不做 ,等后续拓展吧 你们 , 可以忽略掉构造方法 ,这个init
    }


    /**
     * 服务器 启动脚本 固定写法
     */
    @Override
    public void start() throws Exception{

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap
                .group(bossGroup, workerGroup) // 添加组
                .channel(NioServerSocketChannel.class)  // 添加管道
                .option(ChannelOption.SO_BACKLOG, 1024)  // 设置TCP连接数队列
                .childHandler(new ChatServerInitializer(totalConnection, redisPool, listener, address)); //设置初始化项目


        serverBootstrap.bind(address).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("[服务器-{}] 启动成功", address.getPort());
                    // SERVER_SUCCESS 事件
                    listener.onChatBootEvent(new ChatBootEvent(ChatBootEvent.SERVER_SUCCESS));
                } else {
                    if (null != future.cause()) {
                        logger.info("[服务器-{}] 启动失败 , 异常信息 : {}", address.getPort(), future.cause().getMessage());
                    }
                    // 添加 SERVER_FAILURE 事件 , 其实无所谓了 更加保障吧
                    listener.onChatBootEvent(new ChatBootEvent(ChatBootEvent.SERVER_FAILURE));

                    //抛出异常 , 让用户管理 是否关闭
                    throw new ConnectionException("[服务器] 启动失败", future.cause());
                }
            }
        });
    }


    /**
     * 关闭两个线程组 ,当失败的时候 ,
     *
     * @throws Exception
     */
    @Override
    public void shutDown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

}
