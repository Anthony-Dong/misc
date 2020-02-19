package com.chat.core.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chat.core.util.JsonUtil;
import com.chat.core.util.RouterUtil;
import org.junit.Test;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class NPackTest {

    @Test
    public void buildWithStringBody() throws Exception {

        NPack nPack = NpackBuilder.buildWithJsonBody("aaa", "aaaa", NpackBuilder.buildWithStringBody("a", "a", "aaa"));

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

        NPack nPack = NpackBuilder.buildWithStringBody("a", "b", "aaa");

        byte[] body = nPack.getBody();

        System.out.println(new String(body));

        System.out.println(nPack);

    }

    @Test
    public void buildWithJsonBody() throws IOException {


    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(100);
        MessagePack messagePack = new MessagePack();


        long start = System.currentTimeMillis();
        IntStream.range(0,1000).forEach(e-> service.execute(()->{
            Map<String,String> map = new HashMap<>();
            map.put("name", "value");
            map.put("key", e + "");
            String json = JSON.toJSONString(map);
            NPack pack = new NPack(URL.encode(new URL("http", "localhost", 6379, map).toString()), json.getBytes());
            try {
                byte[] write = messagePack.write(pack);
                NPack read = messagePack.read(write, NPack.class);
                System.out.println(URL.valueOfByDecode(read.getRouter())+" : "+JSON.parseObject(new String(read.getBody()),new TypeReference<Map<String,String>>(){}));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));

        service.shutdown();

        service.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);


        System.out.println("end : " + (System.currentTimeMillis() - start));

    }
}