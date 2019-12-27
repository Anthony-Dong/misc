package com.chat.server.context;

import com.chat.core.exception.ContextException;
import com.chat.core.netty.Constants;
import com.chat.server.handler.ChatServerContext;
import com.chat.server.util.RedisPool;
import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.Jedis;

import java.net.InetSocketAddress;

/**
 * @date:2019/12/26 17:26
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class DefaultChatServerContext extends ChatServerContext {

    private final RedisPool pool;

    private final String key;

    private String h_key;

    private final HandlerOtherOperation operation;

    public DefaultChatServerContext(String contextName, HandlerOtherOperation operation) {
        super(contextName);
        this.pool = RedisPool.loadRedisPool();
        this.key = Constants.VERSION_PREFIX + Constants.PROTOCOL_VERSION;
        this.operation = operation;
    }

    @Override
    protected void onStart(InetSocketAddress address) throws ContextException {
        Jedis jedis = null;
        try {
            jedis = pool.get();
            this.h_key = RedisPool.redisKeyName(address);
            jedis.hset(key, h_key, "0");
        } finally {
            pool.remove(jedis);
        }
    }

    @Override
    protected void onFail(InetSocketAddress address) throws ContextException {
        Jedis jedis = null;
        try {
            jedis = pool.get();
            jedis.hdel(key, h_key);
        } finally {
            pool.remove(jedis);
        }
    }


    @Override
    protected void onRemove(ChannelHandlerContext context) throws ContextException {
        Jedis jedis = null;
        try {
            jedis = pool.get();
            String num = jedis.hget(key, h_key);
            int pnum = Integer.parseInt(num.trim());
            // -1
            pnum = pnum - 1;
            jedis.hset(key, h_key, "" + pnum);
            operation.onRemove(context, jedis);
        } finally {
            pool.remove(jedis);
        }
    }


    @Override
    protected void onRegister(ChannelHandlerContext context) throws ContextException {
        Jedis jedis = null;
        try {
            jedis = pool.get();
            String num = jedis.hget(key, h_key);
            int pnum = Integer.parseInt(num);
            //+1
            pnum = pnum + 1;
            jedis.hset(key, h_key, "" + pnum);
            operation.onRegister(context, jedis);
        } finally {
            pool.remove(jedis);
        }
        //移除
    }
}
