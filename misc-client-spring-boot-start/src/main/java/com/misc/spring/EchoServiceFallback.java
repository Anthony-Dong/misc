package com.misc.spring;

import com.misc.core.test.EchoService;

/**
 * todo
 *
 * @date: 2020-05-24
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class EchoServiceFallback implements EchoService {

    @Override
    public int[] hash(String str) {
        return new int[]{1, 2, 3};
    }
}
