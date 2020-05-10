package com.misc.client.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.misc.core.model.netty.Arg;
import com.misc.json.Demo;
import com.misc.json.Inter;
import com.misc.json.TestBean;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * TODO
 *
 * @date:2020/2/18 20:39
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class TestJson {


    public static void main(String[] args) throws IllegalAccessException, InstantiationException {

        Map<String, String> map = Collections.singletonMap("name", "tom");
        List<Object> objects = Collections.emptyList();

        Set<String> singleton = Collections.singleton("1");

//        Demo chat = Demo.chat("1");

        Arg of = Arg.of(0, singleton);

        System.out.println(of.getClazz());

//        of.getClazz().newInstance();


        String value = of.getValue();
        Object object = JSON.parseObject(value, of.getClazz());

        System.out.println(object.getClass());
        System.out.println(object);

    }

    @Test
    public void testReader() throws FileNotFoundException {
        JSONReader reader = new JSONReader(new FileReader("C:\\Users\\12986\\Desktop\\test.json"));
        reader.config(Feature.AllowArbitraryCommas, true);
        reader.startObject();
        Map map = reader.readObject(Map.class);
        System.out.println(map);

        reader.endObject();
        reader.close();

    }


    @Test
    public void testWriter() throws IOException {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("a", "1111");
        hashMap.put("b", "1234");
        hashMap.put("c", "1234");


        // 1. JSONWriter 配置启动
        JSONWriter writer = new JSONWriter(new FileWriter("C:\\Users\\12986\\Desktop\\test.json"));
        writer.config(SerializerFeature.WriteClassName, true);
        writer.startObject();

        Set<Map.Entry<String, Object>> entries = hashMap.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            writer.writeKey(entry.getKey());
            writer.writeValue(entry.getValue());
        }
        // 2.关闭, 释放资源
        writer.endObject();
        writer.close();
    }

    @Test
    public void test3() {
        TestBean testBean = TestBean.of("111111111111", 11111);
        String json = JSON.toJSONString(testBean);
        System.out.println(json);
        TestBean testBean1 = JSON.parseObject(json, TestBean.class);
        System.out.println(testBean1);
    }

    @Test
    public void test4() {


        Inter chat = Demo.instance();
        String json = JSON.toJSONString(chat);

        Inter testBean1 = JSON.parseObject(json, chat.getClass());

        System.out.println(testBean1);

//
//        TestBean testBean = new TestBean("1");
//        String json = JSON.toJSONString(testBean);
//        System.out.println(json);
//        TestBean testBean1 = JSON.parseObject(json, TestBean.class);
//        System.out.println(testBean1);
    }


}
