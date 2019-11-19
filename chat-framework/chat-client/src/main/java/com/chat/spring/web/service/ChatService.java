package com.chat.spring.web.service;

import com.chat.client.model.ChatTemplate;

import com.chat.client.model.SendResult;
import com.chat.spring.listener.SaveChatEntityEvent;
import com.chat.client.util.RedisPool;
import com.chat.core.model.ChatEntity;
import com.chat.core.model.NPack;
import com.chat.core.util.JsonUtil;
import com.chat.core.util.Snowflake;
import com.chat.spring.model.ChatClientProperties;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @date:2019/11/13 16:55
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Service
public class ChatService {

    @Autowired
    private ChatTemplate template;


    @Autowired
    private ChatClientProperties properties;


    @Autowired
    private ApplicationContext application;

    /**
     * 这个是send 发送给 服务器端
     *
     * @param entity
     */
    public void send(@NonNull ChatEntity entity, DeferredResult<SendResult> result, String oldUUID) {
        NPack nPack = new NPack();
        nPack.setRouter(entity.getReceiver());
        nPack.setJson(JsonUtil.toJSONString(entity));


        List<ChannelFuture> channelFutures = template.getChannelFutures();


        int size = channelFutures.size();

        int index = nPack.hashCode() % size;

        ChannelFuture sfuture = channelFutures.get(index);


        try {
            sfuture.channel().writeAndFlush(nPack).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {

                        System.out.println("发送成功");

                        TimeUnit.MILLISECONDS.sleep(200);

                        removeUUID(properties.getRedisPool(), oldUUID);

                        long uuid = generaUUID(properties.getRedisPool());

                        result.setResult(SendResult.success(uuid));

                        // 保存看怎么做吧 ---- > 最好异步 -- >
                        application.publishEvent(new SaveChatEntityEvent(entity));
                    } else {
                        System.out.println("发送失败");

                        result.setErrorResult(SendResult.error);

                        channelFutures.remove(sfuture);

                        // 关闭他直接 ....
                        sfuture.channel().closeFuture();
                    }
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    private static final String UUID_KEY = "UUID_KEY";

    private static final String VALUE = "name";

    private static final Snowflake uuid = Snowflake.of();

    public long generaUUID(RedisPool pool) {

        long uu = uuid.nextId();
        Jedis jedis = pool.get();

        jedis.hset(UUID_KEY, "" + uu, VALUE);

        pool.remove(jedis);
        return uu;
    }


    public void removeUUID(RedisPool pool, String UUID) {
        Jedis jedis = pool.get();

        String hget = jedis.hget(UUID_KEY, UUID);

        if (null != hget) {
            jedis.hdel(UUID_KEY, UUID);
        }
        pool.remove(jedis);
    }


    /**
     * 这个是将他发送出去保存到一个数据库中
     */
    public void saveChatEntity(ChatEntity entity) {
        application.publishEvent(new SaveChatEntityEvent(entity));
    }
}
