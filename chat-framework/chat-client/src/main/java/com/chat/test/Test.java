package com.chat.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @date:2020/2/21 16:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Test {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        long start = System.currentTimeMillis();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        }).start();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        }).start();

        latch.await();
        System.out.println(System.currentTimeMillis()-start);

    }


}
