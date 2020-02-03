package com.netty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @date:2020/2/2 21:10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Demo {

    public static void main(String[] args) throws Exception{
        int rate = 960;

        if (rate > 950) {
            System.out.println("rate");
            TimeUnit.MILLISECONDS.sleep(100);
        } else if (rate > 900) {
            System.out.println("=============");
            TimeUnit.MILLISECONDS.sleep(1);
        }
    }


}
