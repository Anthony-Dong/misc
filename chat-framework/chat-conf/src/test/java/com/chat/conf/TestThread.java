package com.chat.conf;

import com.chat.conf.model.ConfConstant;
import org.junit.Test;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @date:2019/11/12 22:42
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class TestThread {

    @Test
    public void test(){



    }


    public static void main(String[] args) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10,new ChatThreadFactory(ConfConstant.CONF_SCHEDULE_EXECUTOR));


        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "   :  " + System.currentTimeMillis());
            }

        }, 0, 100, TimeUnit.MICROSECONDS);
    }



}
