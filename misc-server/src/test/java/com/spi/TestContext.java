package com.spi;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * TODO
 *
 * @date:2020/2/16 12:49
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class TestContext {


    @Test
    public void testAddr(){
        InetSocketAddress a1 = new InetSocketAddress("localhost",999);
        System.out.println(a1.hashCode());


        InetSocketAddress a2 = new InetSocketAddress("localhost",999);

        System.out.println(a2.hashCode());

        // hashcode
        HashMap<InetSocketAddress, Object> map = new HashMap<>();


        map.put(a1, null);

        map.put(a2, null);

        System.out.println(map.size());

    }

}
