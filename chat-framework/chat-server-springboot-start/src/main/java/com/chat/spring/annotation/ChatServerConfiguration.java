package com.chat.spring.annotation;


import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.chat.spring.env.ChatServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @date:2019/11/10 18:42
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@AutoConfigureAfter(value = {ChatServerProperties.class})
@EnableConfigurationProperties(ChatServerProperties.class)
@Configuration
public class ChatServerConfiguration {

    public static final String chatRedisTemplate = "CHAT_REDIS_TEMPLATE";

    private static final String contextName = "contextName";

    private static final String version = "version";

    private final ChatServerProperties properties;

    @Autowired
    public ChatServerConfiguration(ChatServerProperties properties) {
        this.properties = properties;
    }

    @Bean(chatRedisTemplate)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(factory);
        template.setValueSerializer(new GenericFastJsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericFastJsonRedisSerializer());
        return template;
    }

    @Bean(contextName)
    public String context() {
        return properties.getContextName();
    }

    @Bean(version)
    public short aShort() {
        return properties.getVersion();
    }
}
