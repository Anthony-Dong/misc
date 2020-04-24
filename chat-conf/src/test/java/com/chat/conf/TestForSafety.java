package com.chat.conf;

import org.junit.Test;

import java.util.HashSet;
import java.util.stream.IntStream;

/**
 * TODO
 *
 * @date:2019/11/13 19:30
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class TestForSafety {


    private HashSet<Integer> integers = new HashSet<>();

    @Test
    public void test(){


        IntStream.range(0,1000).forEach(e->{
            integers.add(e);
        });


        System.out.println(integers.size());

    }


}
