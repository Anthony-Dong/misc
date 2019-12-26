package com.chat.server.util;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.util.List;

import static org.junit.Assert.*;

public class RedisPoolTest {

    @Test
    public void test() throws Exception {

        RedisPool redisPool = new RedisPool(1, new HostAndPort("localhost", 6379));

        Jedis jedis = redisPool.get();

        jedis.flushAll();

        String key = "key";

        jedis.lpush(key, "1");

        jedis.lpush(key, "2");

        jedis.lpush(key, "3");


        System.out.println("jedis.lpop(key) = " + jedis.rpop(key));
        System.out.println("jedis.lpop(key) = " + jedis.rpop(key));

        System.out.println("jedis.lpop(key) = " + jedis.rpop(key));

    }
}