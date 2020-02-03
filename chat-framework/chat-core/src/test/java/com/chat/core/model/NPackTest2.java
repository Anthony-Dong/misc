package com.chat.core.model;

import org.junit.Test;
import org.msgpack.MessagePack;

import java.io.IOException;

import static org.junit.Assert.*;

public class NPackTest2 {


    @Test
    public void test() throws IOException {
        MessagePack messagePack = new MessagePack();


        NPack nPack = NPack.buildWithStringBody("a", "b", "c");


        byte[] write = messagePack.write(nPack);

        NPack read = messagePack.read(write, nPack.getClass());

        System.out.println(read);
        read.release();
        System.out.println(read);

        NPack pack = new NPack();

        pack.release();

    }

}