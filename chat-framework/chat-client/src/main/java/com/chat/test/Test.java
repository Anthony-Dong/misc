package com.chat.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
        Logger logger = LoggerFactory.getLogger(Test.class);
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS")));

        Runnable runnable = () -> {
            for (int x = 0; x < 50000; x++) {
                logger.debug("current-time : {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS")));
            }
        };
        new Thread(runnable).start();
        new Thread(runnable).start();
    }


}
