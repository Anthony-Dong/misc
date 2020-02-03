package com.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * TODO
 *
 * @date:2020/2/3 0:04
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Client {


    public static void main(String[] args) {

        Bootstrap bootstrap = new Bootstrap();

        ChannelDuplexHandler duplexHandler = new MyClientChannelDuplexHandler();
        NioEventLoopGroup work = new NioEventLoopGroup();
        Bootstrap handler = bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new FixedLengthFrameDecoder(Long.BYTES));
                        // 自己的Handler
                        pipeline.addLast(duplexHandler);
                    }
                });
        try {
            ChannelFuture future = handler.connect(new InetSocketAddress(10086)).sync();

            System.out.println("connect = localhost:10086");

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
        }
    }

    @ChannelHandler.Sharable
    private static class MyClientChannelDuplexHandler extends ChannelDuplexHandler {

        private AtomicInteger count = new AtomicInteger(0);

        private LongAdder spend = new LongAdder();

        long start_spend = 0;
        int start_count = 0;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    ctx.writeAndFlush(Unpooled.copyLong(System.currentTimeMillis()));
                }
            }).start();

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long a = spend.sum();
                    long sum = a - start_spend;
                    start_spend = a;
                    int b = MyClientChannelDuplexHandler.this.count.get();
                    int count = b - start_count;
                    start_count = b;
                    try {
                        long avg = sum / count;
                        System.out.printf("QPS : %d , avg : %d, count-all : %d\n", count, avg, MyClientChannelDuplexHandler.this.count.get());
                    } catch (Exception e) {
                        //
                    }
                }
            }, 1000, 1000);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            long cost = buf.readLong();
            count.incrementAndGet();
            spend.add(cost);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }
}
