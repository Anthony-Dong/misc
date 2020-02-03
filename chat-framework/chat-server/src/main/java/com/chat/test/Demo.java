package com.chat.test;

import com.chat.core.util.NamedThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @date:2020/1/21 15:54
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Demo {

    private static final ScheduledExecutorService expireExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("Server-Register", true));


    public static void main(String[] args) throws Exception {

        expireExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println("eeeeeeeeeee");
            }
        }, 1000,1000, TimeUnit.MILLISECONDS);


        TimeUnit.SECONDS.sleep(100000);
    }


    static void test() throws Exception {
        test1();
    }

    static void test1() throws RuntimeException {
        test2();
    }

    static void test2() throws A {
        throw new A("A");
    }
}

class A extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public A(String message) {
        super(message);
    }
}
