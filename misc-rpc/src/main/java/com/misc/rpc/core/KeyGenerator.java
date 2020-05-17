package com.misc.rpc.core;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 用来生产key
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface KeyGenerator {

    /**
     * string 的原因是通用
     */
    String getKey();


    AtomicLong counter = new AtomicLong();

    KeyGenerator DEFAULT_KEY_GENERATOR = () -> String.valueOf(counter.incrementAndGet());

}
