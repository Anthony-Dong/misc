package com.chat.core.model.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ArgsUtilTest {
    private static final TypeReference<List<Arg>> TYPE = new TypeReference<List<Arg>>() {

    };

    @Test
    public void convert() throws InvocationTargetException, IllegalAccessException {

        String s = ArgsUtil.convertArgs(null);

        byte[] bytes = s.getBytes();

        for (byte aByte : bytes) {
            System.out.println(aByte);
        }

        String s1 = new String(bytes);


        List<Arg> args = JSON.parseObject(s1, TYPE);

        System.out.println(args);


    }

    private static Method getMethod(Object obj, String mn) {
        Method[] methods = obj.getClass().getMethods();
        Method res = null;
        for (Method method : methods) {
            String name = method.getName();
            if (name.equals(mn)) {
                res = method;
                break;
            }
        }
        return res;
    }

    public void test4(Map<String, Object> msg, List<String> name) {
        System.out.println(msg.getClass());
        System.out.println(name.getClass());
        msg.forEach(new BiConsumer<String, Object>() {
            @Override
            public void accept(String s, Object o) {
                System.out.println(s + " : " + o);
            }
        });
        name.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("list :" + s);
            }
        });
    }


    @Test
    public void convert1() throws InvocationTargetException, IllegalAccessException {
        ArgsUtilTest test = new ArgsUtilTest();

        Method test4 = getMethod(test, "test4");


        Map<String, Object> map = new HashMap<>();
        map.put("1", Collections.singletonMap("o", "v"));

        List<String> list = Arrays.asList("a", "b", "v", "d");

        String args = ArgsUtil.convertArgs(map, list);

        System.out.println(args);

        System.out.println(args.getBytes().length);

        Object[] convert = ArgsUtil.convert(args, test4);


        test4.invoke(test, convert);

        test.getClass().getMethods();

    }
}