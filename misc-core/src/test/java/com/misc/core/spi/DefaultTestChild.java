package com.misc.core.spi;

/**
 * TODO
 *
 * @date:2020/2/16 13:55
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class DefaultTestChild implements TestChild{


    @Override
    public void echo(String msg) {
        System.out.println("default");
    }
}
