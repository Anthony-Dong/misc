package com.netty;

import com.chat.core.util.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        NioEventLoopGroup work = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new FixedLengthFrameDecoder(Long.BYTES));

                        // 自己的Handler
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
        static final ExecutorService service = Executors.newFixedThreadPool(50);


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            long start = buf.readLong();

            service.execute(() -> {
                try {
                    handlerMsg(start, ctx);
                } catch (InterruptedException e) {
                    //
                }
            });
        }

        private void handlerMsg(long start, ChannelHandlerContext ctx) throws InterruptedException {
            int rate = new Random().nextInt(1000);
            long spend;
            if (rate > 990) {
                // 模拟处理时间为 100ms
                TimeUnit.MILLISECONDS.sleep(500);
                spend = 1000 + (System.currentTimeMillis() - start);
            } else if (rate > 950) {
                TimeUnit.MILLISECONDS.sleep(100);
                spend = 500 + (System.currentTimeMillis() - start);
            } else {
                spend = System.currentTimeMillis() - start;
            }
            System.out.printf("thread : %s , start : %d , cost : %d !\n", Thread.currentThread().getName(), start, spend);
            ctx.writeAndFlush(Unpooled.copyLong(spend));
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
