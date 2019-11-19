package com.example.springbootclient;


import com.chat.client.util.RedisPool;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;


public class SpringBootClientApplicationTests {


    @Test
    public void contextLoads() {

        RedisPool redisPool = new RedisPool(5, new HostAndPort("47.93.251.248", 6379));


        Jedis jedis = redisPool.get();


        jedis.hset("hash", "1234", "1");


        String hash = jedis.hget("hash", "1234");


        System.out.println(hash);

    }

}
