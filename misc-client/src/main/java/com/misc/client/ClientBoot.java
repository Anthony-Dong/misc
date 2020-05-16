package com.misc.client;

import com.misc.client.netty.MiscServerClient;
import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.core.exception.HandlerException;
import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyEventListener;
import com.misc.core.netty.NettyClient;
import com.misc.core.netty.NettyConvertHandler;
import com.misc.core.proto.misc.common.MiscSerializableType;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;


/**
 * TODO
 *
 * @date:2020/2/28 13:09
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientBoot {

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
            }

            @Override
            public void disconnected(Channel channel) throws HandlerException {
                System.out.println("disconnected");
            }

            @Override
            public void sent(Channel channel, String message) throws HandlerException {
                System.out.println("sent");
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
        MiscServerClient<String, String> client = new MiscServerClient<>(properties);
        client.setPort(9999);
        client.setNettyEventListener(channelHandler);
        client.setNettyConvertHandler(handler);
        NettyClient<MiscPack, MiscPack, String, String> build = client.build();
        build.start();
        build.getChannel().writeAndFlush("helll world");
        build.sync();
    }
}
