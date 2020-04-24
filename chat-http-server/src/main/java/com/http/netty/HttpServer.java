package com.http.netty;


import com.chat.core.ServerNode;
import com.chat.core.util.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.OpenSslEngine;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;

/**
 * TODO
 *
 * @date:2019/12/18 19:57
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class HttpServer extends ServerNode {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;


    private final InetSocketAddress address;

    // BIND IP
    protected HttpServer(InetSocketAddress address) {
        this.address = address;

        // 事件循环组
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()
                , new NamedThreadFactory("http-server"));
    }

    /**
     * 启动
     *
     * @throws Exception
     */
    @Override
    protected void start() throws Exception {

        // 启动器
        ServerBootstrap bootstrap = new ServerBootstrap();

        final HttpProcessHandler handler = new HttpProcessHandler();

        bootstrap.group(bossGroup, workerGroup)
                // NioServerSocketChannel 处理器
                .channel(NioServerSocketChannel.class)
                // 子处理器
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        ChannelPipeline pipeline = channel.pipeline();

                        // 解码器 一
                        pipeline.addLast(new HttpServerCodec());

                        // 解码器 二 : 请求体最大多少
                        pipeline.addLast(new HttpObjectAggregator(1024));

                        // 解码器 三  : 自定义的
                        pipeline.addLast(handler);
                    }
                })
                // 子选项 , 数据连接成更大的报文来最小化所发送的报文的数量 , 心跳检测
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            // bind端口
            ChannelFuture future = bootstrap.bind(address).sync();

            // 打印日志
            logger.info("服务器启动成功 , URL : http://{}:{} ", address.getHostName(), address.getPort());

            // 关闭管道
            future.channel().closeFuture().sync();
        } finally {
            logger.info("服务器关闭, host is {} , 8087 is {}.", address.getHostName(), address.getPort());
            // 销毁
            shutDown();
        }
    }

    /**
     * 服务关闭
     *
     * @throws Exception 所有的异常都不归我们去管理
     */
    protected void shutDown() throws Exception {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
    }


    public static void run(InetSocketAddress address) throws Exception {
        new HttpServer(address).start();
    }


    public static void run(int port) throws Exception {
        run(new InetSocketAddress(port));
    }
}
