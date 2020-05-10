package com.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * TODO
 *
 * @date:2019/12/27 22:14
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Demo {


    public static void main(String[] args) {

        ServerBootstrap bootstrap = new ServerBootstrap();

        ChannelFuture bind = bootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup(4))
                .channel(NioServerSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {

                            /**
                             * Return {@code true} if the implementation is {@link Sharable} and so can be added
                             * to different {@link ChannelPipeline}s.
                             */
                            @Override
                            public boolean isSharable() {
                                return true;
                            }

                        });
                        // DO
                    }
                })
                .bind(8888);
    }
}
