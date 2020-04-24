package com.chat.core.register;

import com.chat.core.netty.PropertiesConstant;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.junit.Assert.*;

public class DefaultRegistryServiceTest {

    @Test
    public void register() {


        JedisPool jedisPool = new JedisPool("localhost", 6379);


        Jedis redis = jedisPool.getResource();


        String key = PropertiesConstant.CLIENT_REGISTER_KEY +1;


        Map<String, String> map = redis.hgetAll(key);
        map.forEach((s, s2) -> System.out.println(s + " ," + s2));
    }

    @Test
    public void testSring(){
        StringBuilder builder = new StringBuilder();

        StringBuilder append = builder.append(System.currentTimeMillis());

        System.out.println(append.toString());

        append.setLength(0);

        System.out.println("result"+append.toString());

    }
}