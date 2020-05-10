package com.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * TODO
 *
 * @date:2020/1/21 14:24
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class NettyClient {


    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new ChannelInitializer<Channel>() {

                    /**
                     * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward
                     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
                     * <p>
                     * Sub-classes may override this method to change behavior.
                     *
                     * @param ctx
                     * @param msg
                     */
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println(msg);
                    }

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelDuplexHandler() {

                            @Override
                            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("channelRegistered");
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                                ctx.writeAndFlush(Unpooled.copiedBuffer("aaa", CharsetUtil.UTF_8));
                            }
                        });
                    }
                });


        ChannelFuture future = bootstrap.connect("127.0.0.1", 8888);

        boolean ret = future.awaitUninterruptibly(1000, MILLISECONDS);

        if (ret && future.isSuccess()) {
            System.out.println("==============");
        } else {
            System.out.println("========fail=====");
        }
    }
}
