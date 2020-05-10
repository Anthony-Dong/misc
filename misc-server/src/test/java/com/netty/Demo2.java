package com.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * TODO
 *
 * @date:2020/2/25 22:14
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Demo2 {

    public static void main(String[] args) {

        ByteBuf buf = Unpooled.directBuffer(12);

        int i = buf.readInt();
        System.out.println(i);


    }


}
