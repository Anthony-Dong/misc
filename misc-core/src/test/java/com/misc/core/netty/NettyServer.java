package com.misc.core.netty;

import com.sun.org.apache.xpath.internal.operations.Gt;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.junit.Test;
import sun.reflect.generics.tree.VoidDescriptor;

import java.nio.charset.StandardCharsets;

/**
 * todo
 *
 * @date: 2020-05-15
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ServerBootstrap group = serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
        group.channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ChannelDuplexHandler() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("read-1");
                                super.channelRead(ctx, msg);
                            }

                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                System.out.println("write-1");
                                super.write(ctx, msg, promise);
                            }

                            @Override
                            public void flush(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("flush-1");
                                super.flush(ctx);
                            }
                        });


                        pipeline.addLast(new ChannelDuplexHandler() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("read-2");
                                ByteBuf byteBuf = ctx.alloc().directBuffer();
                                byteBuf.writeCharSequence("byteBuf", StandardCharsets.UTF_8);
                                ctx.writeAndFlush(byteBuf);
                            }

                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                System.out.println("write-2");
                                super.write(ctx, msg, promise);
                            }


                            @Override
                            public void flush(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("flush-2");
                                super.flush(ctx);
                            }
                        });
                    }
                });

        ChannelFuture sync = group.bind(8888).sync();
        sync.channel().closeFuture().sync();
    }

    @Test
    public void test() throws IllegalAccessException, InstantiationException {
        Void aVoid = Void.TYPE.newInstance();
        System.out.println(aVoid);
    }
}
