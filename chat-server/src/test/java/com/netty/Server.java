package com.netty;

import com.alibaba.fastjson.JSON;
import com.chat.core.model.NPack;
import com.chat.core.util.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.handler.ipfilter.UniqueIpFilter;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
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

        NioEventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
        NioEventLoopGroup work = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new DefaultThreadFactory("worker"));
//        IpSubnetFilterRule ipSubnetFilterRule = new IpSubnetFilterRule("192.168.28.1", 6, IpFilterRuleType.REJECT);
//        RuleBasedIpFilter filter = new RuleBasedIpFilter(ipSubnetFilterRule);
//        UniqueIpFilter filter1 = new UniqueIpFilter();


//        GlobalTrafficShapingHandler handler = new GlobalTrafficShapingHandler(new NioEventLoopGroup(), 1024, 1024);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024 * 20)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                        pipeline.addLast(MyChannelDuplexHandler.INSTANCE);

                    }
                });
        ChannelFuture future = null;
        try {
            future = bootstrap.bind(new InetSocketAddress(9999)).sync();
            System.out.println("bind success : localhost:9999");
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


        long start = System.currentTimeMillis();

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String name = ctx.channel().attr(attributeKey).get();
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = ByteBufUtil.getBytes(buf);
            System.out.println(new String(bytes, StandardCharsets.UTF_8).trim() + " : " + name);
            start = System.currentTimeMillis();
//            buf.release();
//            TimeUnit.MILLISECONDS.sleep(1000);
            ctx.fireChannelRead(msg);
        }


        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("耗时 ： "+(System.currentTimeMillis() - start));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//            cause.printStackTrace();
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
            System.out.println("register");
            super.channelRegistered(ctx);
            ctx.channel().attr(attributeKey).set(ctx.channel().remoteAddress().toString());
        }


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

//            ChannelConfig config = ctx.channel().config();


//            new Timer().schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    config.setAutoRead(false);
//                    System.out.println("设置成功 。不可以读了");
//                }
//            }, 2000);
//
//
//            new Timer().schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    config.setAutoRead(true);
//                    System.out.println("设置成功 。可以读了");
//                    ctx.read();
//                }
//            }, 6000);


//            ctx.executor().scheduleAtFixedRate(() -> {
////                System.out.println("=====================");
//
//                NPack pack1 = new NPack("hello world", "tom".getBytes());
//                byte[] bytes1 = JSON.toJSONBytes(pack1);
//                int len = 2 + 1 + 4 + bytes1.length;
//                ByteBuf buffer = ctx.alloc().directBuffer(len);
//                buffer.writeShort(2);
//                buffer.writeByte(1);
//                buffer.writeInt(bytes1.length);
//                buffer.writeBytes(bytes1);
//                // 写出去.
//                ctx.writeAndFlush(buffer);
////                buffer.release();
//            }, 0, 2000, TimeUnit.MILLISECONDS);
//            super.channelActive(ctx);
        }


    }
}
