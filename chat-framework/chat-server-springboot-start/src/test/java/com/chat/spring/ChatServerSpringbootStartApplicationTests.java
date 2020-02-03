package com.chat.spring;

import com.chat.core.exception.HandlerException;
import com.chat.core.model.NPack;
import com.chat.core.register.RegisterFactory;
import com.chat.core.spi.SPIUtil;
import com.chat.server.spi.Filter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.function.BiConsumer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChatServerSpringbootStartApplicationTests {

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Test
    public void contextLoads() {


    }

}
