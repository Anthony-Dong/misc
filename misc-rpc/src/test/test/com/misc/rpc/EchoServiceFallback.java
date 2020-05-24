package com.misc.rpc;

import com.misc.core.test.EchoService;

import java.util.List;

/**
 * todo
 *
 * @date: 2020-05-24
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class EchoServiceFallback implements EchoService {
    @Override
    public int[] hashCodes(int _int, String _string, List<Integer> list) {
        return new int[0];
    }
}
