package com.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteBuffer;

/**
 * TODO
 *
 * @date:2020/3/4 13:20
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class DemoNetty {


    public static void main(String[] args) {

        ByteBuf buf = Unpooled.directBuffer(12);
        ByteBuf buf1 = Unpooled.directBuffer(12);
        buf.writeInt(1);
        buf1.writeInt(2);

        ByteBuf composite = Unpooled.wrappedBuffer(buf, buf1);

        composite.readInt();

        buf1.release();
        System.out.println(composite);

        System.out.println(String.format("release: %s, ref: %d.", composite.release(), composite.refCnt()));

        System.out.println(composite);
    }


}
