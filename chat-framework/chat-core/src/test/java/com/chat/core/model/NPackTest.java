package com.chat.core.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chat.core.util.JsonUtil;
import com.chat.core.util.RouterUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.*;

public class NPackTest {

    @Test
    public void buildWithStringBody() throws Exception {

        NPack nPack = NPack.buildWithJsonBody("aaa", "aaaa", NPack.buildWithStringBody("a", "a", "aaa"));

        Properties properties = RouterUtil.convertRouter(nPack.getRouter());
        String type = properties.getProperty("type");
        String classname = properties.getProperty("classname");
        System.out.println("type = " + type);
        System.out.println("classname = " + classname);


        // JSON
        byte[] body = nPack.getBody();
        String json = new String(body);
        Class<?> clazz = Class.forName(classname);
        Object object = JSON.parseObject(json, clazz);


        System.out.println("parse : "+object);
    }

    @Test
    public void buildWithByteBody() {

        NPack nPack = NPack.buildWithStringBody("a", "b", "aaa");

        byte[] body = nPack.getBody();

        System.out.println(new String(body));

        System.out.println(nPack);

    }

    @Test
    public void buildWithJsonBody() {

    }
}