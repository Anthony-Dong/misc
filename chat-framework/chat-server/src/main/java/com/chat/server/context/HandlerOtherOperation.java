package com.chat.server.context;

import com.chat.core.exception.ContextException;
import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.Jedis;

/**
 * @date:2019/12/27 10:09
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public interface HandlerOtherOperation {

    default void onRemove(ChannelHandlerContext context, Jedis jedis) throws ContextException {

    }

    default void onRegister(ChannelHandlerContext context, Jedis jedis) throws ContextException {

    }
}
