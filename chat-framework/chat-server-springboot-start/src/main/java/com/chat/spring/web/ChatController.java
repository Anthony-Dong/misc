package com.chat.spring.web;

import com.chat.core.model.HttpResponse;
import com.chat.spring.pojo.MessageDo;
import com.chat.spring.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @date:2019/12/27 22:01
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@RequestMapping("/api/chat")
@RestController
public class ChatController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChatService service;

    @DeleteMapping("/get/{id}")
    public Object test(@PathVariable("id") String id) {
        BoundListOperations<String, Object> ops = redisTemplate.boundListOps(id);
        return ops.leftPop();
    }


    @PostMapping("/put/msg")
    public HttpResponse putMessage(@RequestBody MessageDo messageDo) {
        return service.insert(messageDo);
    }
}
