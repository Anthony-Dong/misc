package com.chat.core.spi;

import org.junit.Test;

import static org.junit.Assert.*;

public class SPIUtilTest {

    @Test
    public void test() {
        TestFather testFather = SPIUtil.loadClass(TestFather.class, SPIUtilTest.class.getClassLoader());

        testFather.echo("aaa");
    }
}