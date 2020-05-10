package com.misc.core.spi;

import org.junit.Test;

public class SPIUtilTest {

    @Test
    public void test() {
        TestFather testFather = SPIUtil.loadClass(TestFather.class, SPIUtilTest.class.getClassLoader());

        testFather.echo("aaa");
    }

    @Test
    public void loadFirstInstanceOrDefault() {

        TestChild testChild = SPIUtil.loadFirstInstanceOrDefault(TestChild.class, DefaultTestChild.class);


        testChild.echo("ok");
    }
}