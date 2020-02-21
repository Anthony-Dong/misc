package com.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

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
            future = bootstrap.bind(10086).sync();
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


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            FileOutputStream stream = new FileOutputStream("D:\\代码库\\分布式聊天框架\\chat-framework\\office.iso", true);
            FileChannel channel = stream.getChannel();
            System.out.println("buf : " + buf + "channel-size : " + channel.size());
            buf.readBytes(channel, channel.size(), (buf.writerIndex()-buf.readerIndex()));
            System.out.println(buf);
            channel.close();
            stream.close();
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            //
        }
    }
}
