package com.misc.core.model;

import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class URLTest {

    @Test
    public void test() {
        URL url = URL.valueOf("http://www.baidu.com/hell?m1.name=v1,v2&k2=v2&m1.param=v1");
        System.out.println("url.getParameter(\"k1\") = " + url.getParameter("k1"));

        System.out.println("url.getHost() = " + url.getHost());

        List<String> parameter = url.getParameter("m1.names", Collections.EMPTY_LIST);

        System.out.println(parameter);


        System.out.println(url.getServiceInterface());


        System.out.println(url.getMethodParameter("m1", "name"));


    }
}