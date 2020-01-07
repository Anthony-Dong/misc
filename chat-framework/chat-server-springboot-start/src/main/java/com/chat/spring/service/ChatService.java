package com.chat.spring.service;

import com.chat.core.model.HttpResponse;
import com.chat.core.util.Snowflake;
import com.chat.spring.mapper.MessageRepository;
import com.chat.spring.pojo.MessageDo;
import com.chat.spring.uitl.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 聊天 服务
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


    private static final String user_map = "user:msg:map";


    /**
     * 获取 消息ID  ,在用户登录的时候执行
     *
     * @param sender 消息发送者
     * @return 消息ID
     */
    public HttpResponse getMesId(String sender) {
        // 获取ID
        BoundHashOperations<String, String, Long> ops = redisTemplate.boundHashOps(user_map);
        Long aLong = ops.get(sender);

        // 空直接生成一个,返回
        if (aLong == null) {
            long nextId = SNOWFLAKE.nextId();
            ops.put("" + sender, nextId);
            return HttpResponse.succress(nextId);
        }
        return HttpResponse.succress(aLong);
    }


    /**
     * 插入消息
     *
     * @param messageDo
     * @return
     */
    public HttpResponse insert(MessageDo messageDo) {
        Long id = messageDo.getId();

        Long senderId = messageDo.getSenderId();
        String sender = senderId.toString();

        // 查看ID
        BoundHashOperations<String, String, Long> ops = redisTemplate.boundHashOps(user_map);
        Long aLong = ops.get(sender);


        if (aLong == null) {
            return HttpResponse.fail(getMesId(sender));
        }

        // 2. 保存数据
        if (aLong.longValue() == id.longValue()) {
            BoundListOperations<String, Object> listOps = redisTemplate.boundListOps("" + messageDo.getReceiverId());
            MessageUtil.saveMessage(listOps, messageDo);
            messageRepository.save(messageDo);
            // 生成ID , 添加进去
            long l = SNOWFLAKE.nextId();
            ops.put(sender, l);
            return HttpResponse.succress(l);
        } else {
            // 否则冲突就返回ID了
            return HttpResponse.fail(id);
        }
    }
}
