package com.chat.core.util;

import com.chat.core.model.NPack;
import org.junit.Test;
import org.msgpack.MessagePack;

import java.io.IOException;


public class MessagePackPoolTest {

    @Test
    public void getPack() throws IOException {

        MessagePack pack = MessagePackPool.getPack();

        NPack nPack = new NPack("router", new String("hello world").getBytes());


        byte[] write = pack.write(nPack);


        NPack read = pack.read(write, NPack.class);


        byte[] body = read.getBody();

        System.out.println("body = " + new String(body));


        System.out.println("read = " + read);
    }
}