package com.misc.core.spi;

import com.misc.core.annotation.Primary;

/**
 * TODO
 *
 * @date:2019/12/26 19:47
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Primary
public class ITestFather implements TestFather {

    static {
        System.out.println("static");
    }

    private TestChild testFather;
    public ITestFather() {
        System.out.println("hello ");
        this.testFather = SPIUtil.loadClass(TestChild.class, ITestFather.class.getClassLoader());
    }

    @Override
    public void echo(String name) {
        System.out.println("父亲 嘤嘤嘤");
        testFather.echo(name);
    }
}
