package com.chat.spring.service;

import com.chat.core.model.HttpResponse;
import com.chat.core.util.Pair;
import com.chat.core.util.Snowflake;
import com.chat.spring.mapper.MessageRepository;
import com.chat.spring.pojo.MessageDo;
import com.chat.spring.uitl.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;
import sun.management.Sensor;

import java.util.Date;
import java.util.Optional;

/**
 * 聊天 服务
 *
 * @date:2020/1/7 16:52
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Service
public class ChatService {

    private static final Snowflake SNOWFLAKE = Snowflake.of();

    @Autowired
    private MessageRepository messageRepository;


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    private static final String user_map = "user_msg_map";

    private static final String lock = "_lock_";


    /**
     * 获取 消息ID  ,在用户登录的时候执行
     *
     * @param senderID 消息发送者 ID
     * @return 消息ID
     */
    public Long getMesId(String sender, BoundHashOperations<String, String, Long> ops) {
        // 获取ID
        Long uuid = ops.get(sender);
        // 空直接生成一个,返回
        if (uuid == null) {
            uuid = System.currentTimeMillis();
            ops.put(sender, uuid);
            return uuid;
        }
        return uuid;
    }


    /**
     * 插入消息
     *
     * @param messageDo 保证消息消费的有序性
     * @return
     */
    public HttpResponse insert(MessageDo messageDo) {
        // 获取用户ID
        Long senderId = messageDo.getSenderId();

        if (senderId == null) {
            return HttpResponse.fail(null);
        }

        String sender = senderId.toString();

        BoundHashOperations<String, String, Long> ops = redisTemplate.boundHashOps(user_map);

        // 获取ID
        Long redisMessageID = getMesId(sender, ops);

        Long sendMessageID = messageDo.getId();
        // 比较 ID 如果 == message ID , 那么就插入,

        if (sendMessageID == null) {
            messageDo.setId(redisMessageID);
            return HttpResponse.fail(messageDo);
        }

        String senderLock = sender + lock + redisMessageID;


        Boolean ok = redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(senderLock.getBytes(), null, Expiration.seconds(5), RedisStringCommands.SetOption.SET_IF_ABSENT));

        if (ok != null && ok) {
            BoundListOperations<String, Object> Rops = redisTemplate.boundListOps(messageDo.getReceiverId().toString());
            // 保存消息
            MessageUtil.saveMessage(Rops, messageDo);

            messageDo.setCreateTime(new Date());
            // 消息
            messageRepository.save(messageDo);
            // 生成ID , 添加进去
            long uuid = System.currentTimeMillis();

            ops.put(sender, uuid);
            redisTemplate.delete(senderLock);
            return HttpResponse.success(uuid);
        } else {
            return HttpResponse.fail(messageDo);
        }
    }
}
