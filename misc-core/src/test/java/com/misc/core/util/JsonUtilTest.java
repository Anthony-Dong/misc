package com.misc.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.misc.core.model.netty.Arg;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.*;

public class JsonUtilTest {

    @Test
    public void toJSONString() {


        Arg arg = new Arg();

        arg.setClazz(String.class);


        arg.setIndex(0);

        arg.setValue("hello world");

        System.out.println(arg);

        String s = JsonUtil.toJSONString(arg);


        Arg arg1 = JSON.parseObject(s, Arg.class);

        System.out.println(arg1.getClazz());
    }

    public ArrayList<Object> test(String s) {
        ArrayList<Object> list1 = JsonUtil.parseObject(s, new TypeReference<ArrayList<Object>>() {

        });

        Type r1 = new Type() {

        };

//        TypeReference<List<String>> r2 = new TypeReference<>();

        Type[] types = new Type[2];

        types[0] = r1;
        return list1;
    }

    public void test(Map<String,String> map,List<String> list){

    }
}