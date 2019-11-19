package com.example.springbootnetty;


import com.chat.server.util.RedisPool;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;


public class SpringBootNettyApplicationTests {


    @Test
    public void test() {

        RedisPool redisPool = new RedisPool(5, new HostAndPort("47.93.251.248", 6379));


        Jedis jedis = redisPool.get();


        Set<String> anxing = jedis.zrevrange("b", 0, 10000000);

        for (String s : anxing) {
            System.out.println(s);
        }
    }


}
