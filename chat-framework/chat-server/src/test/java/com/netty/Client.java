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

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
            System.out.printf("ChannelFuture %d\n", future.channel().hashCode());
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

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.printf("handlerAdded %d\n", ctx.channel().hashCode());
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.printf("channelRegistered %d\n", ctx.channel().hashCode());
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.printf("channelActive %d\n", ctx.channel().hashCode());
            FileInputStream stream = new FileInputStream("D:\\樊浩东\\软件\\office2010.iso");
            FileChannel channel =
                    stream.getChannel();
            ByteBuf buf = Unpooled.directBuffer(1024);
            buf.writeBytes(channel, 0, stream.available());
            System.out.println(buf);
            ctx.channel().writeAndFlush(buf);
            channel.close();
            stream.close();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }
}
