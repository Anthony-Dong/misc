package com.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
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
import java.util.List;
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

                        pipeline.addLast("1", new ChannelDuplexHandler() {
                            int count = 0;

                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                super.write(ctx, msg, promise);

                                System.out.println("write");
                                if (++count % 5 == 0) {
                                    System.out.println("===flush========");
                                    ctx.flush();
                                }
                            }
                        });
                        pipeline.addLast("2", MyClientChannelDuplexHandler2.instance);
                    }
                });
        try {
            ChannelFuture future = handler.connect(new InetSocketAddress(9999)).sync();
            System.out.printf("ChannelFuture %d\n", future.channel().hashCode());
            System.out.println("connect = localhost:9999");
            future.channel().closeFuture().sync();
            System.out.println("失败.........");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
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
        public void channelActive(ChannelHandlerContext ctx) throws Exception {





            System.out.println("channelActive2");
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    String format = "hello world";
                    int length = format.length();
                    ByteBuf buf = ctx.alloc().ioBuffer(length + 4);
                    buf.writeInt(length);
                    buf.writeBytes(format.getBytes());
                    ctx.write(buf);
                }
            }, 0, 1000);
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
            System.out.println(cause.getMessage());
            ctx.close();
        }

    }
}
