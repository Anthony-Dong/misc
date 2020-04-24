package com.chat.core.util;

import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class SnowflakeTest {
    public static void main(String[] args) throws InterruptedException {
        of();
    }

//    @Test
    public  static void of() throws InterruptedException {
        System.out.println(~(-1L << 4));

        ExecutorService service = Executors.newFixedThreadPool(1000);
        Snowflake of = Snowflake.of();

        long start = System.currentTimeMillis();

        IntStream.range(0,100000).forEach(e->{
            service.execute(() -> {

                UUID.randomUUID().toString();
//                long l = of.nextId();
//                System.out.println(l);
            });
        });

        service.shutdown();

        service.awaitTermination(1111, TimeUnit.DAYS);
        System.out.println(System.currentTimeMillis() - start);

    }
}