package com.chat.spring.config;

import com.chat.core.exception.ContextException;
import com.chat.server.handler.ChatServerContext;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @date:2020/1/5 21:50
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Component
public class RedisChatServerContext extends ChatServerContext {

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisChatServerContext(RedisTemplate<String, Object> pool, String contextName, short version) {
        super(contextName, version);
        this.redisTemplate = pool;
    }

    @Override
    protected void onStart(InetSocketAddress address) throws ContextException {

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

    }
}
