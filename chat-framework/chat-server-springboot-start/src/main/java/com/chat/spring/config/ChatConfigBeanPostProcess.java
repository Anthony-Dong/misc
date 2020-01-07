package com.chat.spring.config;

import com.chat.spring.annotation.ChatServerConfiguration;
import org.springframework.beans.BeansException;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @date:2020/1/5 23:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Component
public class ChatConfigBeanPostProcess implements BeanPostProcessor {

    static volatile RedisTemplate<String, Object> redisTemplate;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass() == RedisTemplate.class) {
            if (beanName.equals(ChatServerConfiguration.chatRedisTemplate)) {
                redisTemplate = (RedisTemplate<String, Object>) bean;
            }
        }
//        if (bean.getClass() == RedisChatServerContext.class) {
//            System.out.println("beanName = " + beanName);
//        }

        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
