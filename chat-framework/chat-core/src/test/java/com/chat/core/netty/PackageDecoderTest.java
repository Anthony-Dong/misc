package com.chat.core.netty;

import com.chat.core.model.NPack;
import com.chat.core.model.NpackBuilder;
import com.chat.core.util.MessagePackPool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class PackageDecoderTest {

    @Test
    public void decode() throws Exception {
        // 初始化 pack
        MessagePack pack = MessagePackPool.getPack();


        NPack nPack1 = NpackBuilder.buildWithJsonBody("1111", "22222", "3333");
        byte[] write1 = pack.write(nPack1);
        NPack nPack2 = NpackBuilder.buildWithJsonBody("BBBB", "CCCCC", "DDDD");
        byte[] write2 = pack.write(nPack2);


        // 模拟 in
        ByteBuf buffer = Unpooled.buffer(100);
        int release = buffer.readerIndex();

        buffer.writeShort(Constants.PROTOCOL_VERSION);
        buffer.writeInt(123);


        // 1. 写一个包
        buffer.writeShort(Constants.PROTOCOL_VERSION);
        buffer.writeInt(write1.length);
        buffer.writeBytes(write1, 0, write1.length);

        // 2. 写第二个包
        buffer.writeShort(Constants.PROTOCOL_VERSION);
        buffer.writeInt(write2.length);
        buffer.writeBytes(write2, 0, write1.length);


        // 3. 写可能出错的地方
        buffer.writeShort(Constants.PROTOCOL_VERSION);
        buffer.writeInt(1111);


        // 4. 重置
        buffer.readerIndex(release);


        ArrayList<Object> list = new ArrayList<>();


        PackageDecoder decoder = new PackageDecoder((short) 1);


        decoder.decode(null, buffer, list);


        System.out.println("=====已读======");
        list.forEach(System.out::println);
        System.out.println("==================");


        System.out.println("buffer.refCnt() = " + buffer.refCnt());
        System.out.println("buffer.writerIndex() = " + buffer.writerIndex());

        System.out.println("buffer.readerIndex() = " + buffer.readerIndex());

        System.out.println("buffer.readShort() = " + buffer.readShort());
        System.out.println("buffer.readInt() = " + buffer.readInt());

    }
}