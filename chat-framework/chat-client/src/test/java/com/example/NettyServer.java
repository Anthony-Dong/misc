package com.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @date:2020/1/21 14:30
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class NettyServer {


    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelDuplexHandler() {
                            @Override
                            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("=====channelRegistered=============");
                                ctx.writeAndFlush(Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8));
                            }
                        });
                    }
                });


        ChannelFuture future = bootstrap.bind(8888);

        future.syncUninterruptibly();

        Channel channel = future.channel();
    }
}
