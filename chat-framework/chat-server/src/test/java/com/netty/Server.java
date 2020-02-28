package com.netty;

import com.alibaba.fastjson.JSON;
import com.chat.core.model.NPack;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @date:2020/2/2 21:02
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Server {

    public static void main(String[] args) {

//        NioEventLoopGroup handler = new NioEventLoopGroup(100,new NamedThreadFactory("handler"));

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work).channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_RCVBUF, 1024 * 20)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(MyChannelDuplexHandler.INSTANCE);
                    }
                });
        ChannelFuture future = null;
        try {
            future = bootstrap.bind(new InetSocketAddress("192.168.28.1", 6666)).sync();

            System.out.println("bind success : localhost:10086");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            //
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    @ChannelHandler.Sharable
    private static class MyChannelDuplexHandler extends ChannelDuplexHandler {
        static final ChannelDuplexHandler INSTANCE = new MyChannelDuplexHandler();


        static final AttributeKey<String> attributeKey = AttributeKey.valueOf("ip");


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String name = ctx.channel().attr(attributeKey).get();
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = ByteBufUtil.getBytes(buf);
            System.out.println(new String(bytes).trim() + " : " + name);
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println(cause.getMessage());
            ctx.close();
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            System.out.println("remove");
        }


        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            super.write(ctx, msg, promise);
        }


        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            super.channelRegistered(ctx);
            ctx.channel().attr(attributeKey).set(ctx.channel().remoteAddress().toString());
            System.out.println("register");
        }


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.executor().scheduleAtFixedRate(() -> {
//                System.out.println("=====================");

                NPack pack1 = new NPack("hello world", "tom".getBytes());
                byte[] bytes1 = JSON.toJSONBytes(pack1);
                int len = 2 + 1 + 4 + bytes1.length;
                ByteBuf buffer = ctx.alloc().directBuffer(len);
                buffer.writeShort(2);
                buffer.writeByte(1);
                buffer.writeInt(bytes1.length);
                buffer.writeBytes(bytes1);



                // 写出去.
                ctx.writeAndFlush(buffer);
//                buffer.release();
            }, 0, 2000, TimeUnit.MILLISECONDS);
            super.channelActive(ctx);
        }


    }
}
