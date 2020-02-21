package com.netty;

import com.chat.core.util.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TODO
 *
 * @date:2020/2/3 1:13
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class DemoServer {

    public static void main(String[] args) {

        NioEventLoopGroup boss = new NioEventLoopGroup(1, new NamedThreadFactory("boss"));
        NioEventLoopGroup work = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2, new NamedThreadFactory("worker"));

//        NioEventLoopGroup handler = new NioEventLoopGroup(50, new NamedThreadFactory("handler"));
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 字符串编码器.
                        pipeline.addLast(new StringEncoder());
                        // 每行行解码器.
                        pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
                        // 字符串解码器.
                        pipeline.addLast(new StringDecoder());
                        // 自己的Handler
                        pipeline.addLast(MyChannelDuplexHandler.INSTANCE);
                    }
                });
        ChannelFuture future = null;
        try {
            future = bootstrap.bind(10086).sync();
//            System.out.println("bind success : localhost:10086");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    @ChannelHandler.Sharable
    private static class MyChannelDuplexHandler extends ChannelDuplexHandler {
       // CharSink sink;

        MyChannelDuplexHandler() {
//            sink = Files.asCharSink(new FileAndPackageDecoder("D:\\MyDesktop\\template\\log.txt"), Charset.forName("utf-8"), FileWriteMode.APPEND);
        }
        // 添加一个线程池 .
//        static final ExecutorService service = Executors.newFixedThreadPool(100);

        static ChannelDuplexHandler INSTANCE = new MyChannelDuplexHandler();

        @Override

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            this.func(ctx, msg);

//            service.execute(() -> {
//                try {
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
        }

        private void func(ChannelHandlerContext ctx, Object msg) throws IOException {
            String message = (String) msg;
            long start = Long.parseLong(message.trim());
            System.out.printf("thread : %s ,addr : %s , spend : %dms\n", Thread.currentThread().getName(), ctx.channel().remoteAddress().toString(), System.currentTimeMillis() - start);
            String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss SSS\n"));
            ctx.writeAndFlush(format);
//            sink.write((System.currentTimeMillis() - start) + "\n");
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            //no ting
        }
    }
}