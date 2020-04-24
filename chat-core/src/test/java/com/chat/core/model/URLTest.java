package com.chat.core.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class URLTest {

    @Test
    public void testUrl(){
//        URL url = new URL("msg", "localhost", 6379);


        String utl = "dubbo://192.168.28.1:12345/com.example.dubbo.inter.EchoService?anyhost=true&application=dubbo-consumer&bean.name=providers:dubbo:com.example.dubbo.inter.EchoService&bind.ip=192.168.28.1&bind.port=12345&default.deprecated=false&default.dynamic=false&default.register=true&deprecated=false&dubbo=2.0.2&dynamic=false&generic=false&interface=com.example.dubbo.inter.EchoService&methods=echo,convert&pid=5376&register=true&release=2.7.1&side=provider&timeout=3000000&timestamp=1581922997497";

        URL url = URL.valueOf(utl);

        System.out.println("url.getPath() = " + url.getPath());

        System.out.println("url.getPath() = " + url.getPathKey());

        String echo = url.getMethodParameter(url.getPath(), "echo");


        System.out.println(echo);


        String test = url.getMethodParameter("com.example.dubbo.inter.EchoService", "");

        System.out.println(test);



        System.out.println("url.getParameter(\"name\") = " + url.getParameter("name"));

        System.out.println("url.getParameter(\"id\") = " + url.getParameter("id"));
        System.out.println("url.getProtocol() = " + url.getProtocol());

        System.out.println("url.getHost() = " + url.getHost());
        System.out.println("url.getPort() = " + url.getPort());


//        System.out.println("url.toString() = " + url.toString());

//        System.out.println(url.toFullString());
    }
}