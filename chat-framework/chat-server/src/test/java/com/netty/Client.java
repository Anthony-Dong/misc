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
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
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

//        ChannelDuplexHandler duplexHandler = new MyClientChannelDuplexHandler();
        NioEventLoopGroup work = new NioEventLoopGroup();
        Bootstrap handler = bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
//                        pipeline.addLast(new FixedLengthFrameDecoder(Long.BYTES));
                        // 自己的Handler
                        pipeline.addLast("1", MyClientChannelDuplexHandler1.HANDLER);

                        pipeline.addLast("2", MyClientChannelDuplexHandler2.instance);
                    }
                });
        try {
            ChannelFuture future = handler.connect(new InetSocketAddress("192.168.28.1", 6666)).sync();
            System.out.printf("ChannelFuture %d\n", future.channel().hashCode());
            System.out.println("connect = localhost:10086");

            future.channel().closeFuture().sync();
            System.out.println("失败.........");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
        }
    }

    @ChannelHandler.Sharable
    private static class MyClientChannelDuplexHandler1 extends ChannelDuplexHandler {
        static final MyClientChannelDuplexHandler1 HANDLER = new MyClientChannelDuplexHandler1();

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.printf("handlerAdded1 %d\n", ctx.channel().hashCode());
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.printf("channelRegistered1 %d\n", ctx.channel().hashCode());
            ctx.fireChannelRegistered();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelActive1");
            ctx.fireChannelActive();

            ctx.fireUserEventTriggered("触发了");

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }


        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("write1 .......");
            ByteBuf buffer = Unpooled.buffer(10);
            buffer.writeCharSequence((String) msg, StandardCharsets.UTF_8);
            super.write(ctx, buffer, promise);

        }


        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            System.out.println("flush1 .......");
            super.flush(ctx);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            super.handlerRemoved(ctx);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("event1 : " + evt);
            super.userEventTriggered(ctx, evt);
        }
    }

    @ChannelHandler.Sharable
    private static class MyClientChannelDuplexHandler2 extends ChannelDuplexHandler {
        static final MyClientChannelDuplexHandler2 instance = new MyClientChannelDuplexHandler2();

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.printf("handlerAdded2 %d\n", ctx.channel().hashCode());
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.printf("channelRegistered2 %d\n", ctx.channel().hashCode());
            ctx.fireChannelRegistered();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelActive2");
            ctx.executor().scheduleAtFixedRate(() -> {
                ctx.channel().writeAndFlush(System.getProperty("name"));
            }, 0, 1000, TimeUnit.MILLISECONDS);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            super.handlerRemoved(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("write2 .......");
            super.write(ctx, msg, promise);
        }


        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            super.channelReadComplete(ctx);
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            System.out.println("flush2 .......");
            super.flush(ctx);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("event2 : " + evt);
            super.userEventTriggered(ctx, evt);
        }
    }
}
