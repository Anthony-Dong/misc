package com.misc.core.spi;

/**
 * TODO
 *
 * @date:2019/12/26 19:52
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ITestChild implements TestChild {


    @Override
    public void echo(String msg) {
        System.out.println("嘤嘤嘤 : " + msg);
    }
}
