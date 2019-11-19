package com.chat.spring.web.controller;


import com.alibaba.fastjson.TypeReference;

import com.chat.client.model.SendResult;
import com.chat.client.util.RedisPool;
import com.chat.spring.model.ChatClientProperties;
import com.chat.spring.web.service.ChatService;
import com.chat.core.model.ChatEntity;
import com.chat.core.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 聊天服务中心
 *
 * 我的想法 是 当用户登录时,会获取一个 UUID , 一个字符串 ,每次发送消息需要携带他
 *
 * 当发送成功 , 同时将UUID+1(也可以是其他)响应给用户, 下次携带 响应的ID 防止数据重复发送之类的
 *
 *
 * @date:2019/11/13 16:20
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@RequestMapping("/chat/client")
@RestController
public class ChatClientController {


    @Autowired
    private ChatClientProperties properties;


    @Autowired
    private ChatService service;


    private static final String UUID_KEY = "UUID_KEY";

    /**
     * 每一条消息会有一个UUID ,第一次校验
     */
    @GetMapping("/uuid")
    public long get() {
        return service.generaUUID(properties.getRedisPool());
    }


    /**
     * 通过 UUID 校验此条消息的唯一性 ,
     *
     * @param uuid
     * @param entity
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/send/{uuid}")
    public DeferredResult<SendResult> send(@PathVariable("uuid") String uuid, @RequestBody ChatEntity entity, HttpServletRequest request, HttpServletResponse response) {
        /**
         * 毫秒 - >
         */
        DeferredResult<SendResult> result = new DeferredResult<SendResult>(2000L, SendResult.error);

        RedisPool redisPool = properties.getRedisPool();


        Jedis jedis = redisPool.get();

        String hget = jedis.hget(UUID_KEY, uuid);

        //用UUID来区别信息不重复
        if (null == hget) {
            result.setErrorResult(SendResult.error);
            response.setStatus(403);
            redisPool.remove(jedis);
            return result;
        }


        // 设置IP
        entity.setIp(request.getRemoteHost());

        // 发送消息
        service.send(entity, result, uuid);
        return result;
    }


    @GetMapping("/receive/{name}")
    public List<String> getReceive(@PathVariable("name") String name) {

        RedisPool redisPool = properties.getRedisPool();

        Jedis jedis = redisPool.get();

        Set<String> zrevrange = jedis.zrevrange(name, 0, 8015986);

        List<String> list = new ArrayList<>();

        zrevrange.forEach(e -> {
            ChatEntity chatEntity = JsonUtil.parseObject(e, new TypeReference<ChatEntity>() {
            });
            list.add(chatEntity.getMsg());
        });

        redisPool.remove(jedis);

        return list;
    }
}
