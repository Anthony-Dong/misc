package com.chat.spring.config;

import com.chat.core.exception.ContextException;
import com.chat.core.exception.RegisterException;
import com.chat.core.register.RegisterFactory;
import com.chat.core.util.NamedThreadFactory;
import com.chat.core.util.Pair;
import com.chat.server.handler.ChatServerContext;
import com.chat.spring.annotation.ChatServerConfiguration;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @date:2020/1/5 21:50
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Component
public class RedisChatServerContext extends ChatServerContext {

    private RedisTemplate<String, Object> redisTemplate;

    private static final int EXPIRE_TIME = 30000;

    private final ScheduledExecutorService expireExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("Server-Register", true));


    @Autowired
    public RedisChatServerContext(@Qualifier(value = ChatServerConfiguration.chatRedisTemplate) RedisTemplate<String, Object> pool,
                                  @Qualifier(value = ChatServerConfiguration.contextName) String contextName,
                                  @Qualifier(value = ChatServerConfiguration.version) short version) {
        super(contextName, version);
        this.redisTemplate = pool;
    }

    @Override
    protected void onStart(InetSocketAddress address) throws ContextException {

        expireExecutor.scheduleWithFixedDelay(() -> {

            BoundHashOperations<String, String, Object> hash = redisTemplate.boundHashOps(RegisterFactory.SERVER_KEY);

            long time = System.currentTimeMillis();

            Pair<InetSocketAddress, Integer> pair = new Pair<>();

            pair.setK(address);

            pair.setV(map.size());

            // 添加当前的
            hash.put("" + time, pair);

            Map<String, Object> urls = hash.entries();

            if (urls == null || urls.isEmpty()) {
                return;
            }

            // 删除过期的的
            urls.forEach((s, o) -> {
                long l = Long.parseLong(s);
                if ((time - l) > EXPIRE_TIME) {
                    hash.delete(s);
                }
            });
        }, 0, EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onFail(InetSocketAddress address) throws ContextException {

    }

    @Override
    protected void onRemove(ChannelHandlerContext context) throws ContextException {
        BoundHashOperations<String, Object, Object> user = redisTemplate.boundHashOps("user");
        user.put(context.channel().remoteAddress().toString(), "小王");
    }

    @Override
    protected void onRegister(ChannelHandlerContext context) throws ContextException {
        SocketAddress address = context.channel().remoteAddress();

        BoundValueOperations<String, Object> valueOperations = redisTemplate.boundValueOps(context.channel().remoteAddress().toString());

        Object o = valueOperations.get();

    }
}
