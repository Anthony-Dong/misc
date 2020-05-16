package com.misc.server;

import com.misc.core.exception.HandlerException;
import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyConvertHandler;
import com.misc.core.netty.NettyEventListener;

import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.core.proto.misc.common.MiscSerializableType;
import com.misc.server.netty.MiscNettyServerBuilder;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;


/**
 * 日志设置系统属性 {user.dir}
 */
public class ServerBoot {

    public static void main(String[] args) throws Throwable {
        NettyConvertHandler<MiscPack, MiscPack, String, String> handler = new NettyConvertHandler<MiscPack, MiscPack, String, String>() {
            @Override
            protected String decode(MiscPack msg) {
                return msg.getRouter();
            }

            @Override
            protected MiscPack encode(ByteBufAllocator allocator, String msg) {
                return new MiscPack(msg);
            }
        };


        NettyEventListener<String, String> channelHandler = new NettyEventListener<String, String>() {
            @Override
            public void connected(Channel channel) throws HandlerException {
                System.out.println("connected");
                channel.writeAndFlush("hello world");
            }

            @Override
            public void disconnected(Channel channel) throws HandlerException {
                System.out.println("disconnected");
            }

            @Override
            public void sent(Channel channel, String message) throws HandlerException {
                System.out.println("sent" + message);
            }

            @Override
            public void received(Channel channel, String message) throws HandlerException {
                System.out.println("received " + message);
            }

            @Override
            public void caught(Channel channel, Throwable exception) throws HandlerException {
                System.out.println("e" + exception);
            }
        };

        MiscProperties properties = new MiscProperties();
        properties.setVersion((short) 10);
        properties.setSerialType(MiscSerializableType.BYTE_ARRAY);

        MiscNettyServerBuilder<String, String> server = new MiscNettyServerBuilder<>(properties);

        server.setPort(9999);

        server.setNettyConvertHandler(handler);

        server.setNettyEventListener(channelHandler);

        server.build().start().sync();
    }
}
