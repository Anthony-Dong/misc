package com.misc.server.spi.handler;

import com.misc.core.exception.HandlerException;
import com.misc.core.model.netty.Request;
import io.netty.channel.ChannelHandlerContext;

/**
 * {@link RequestHandler} 处理器
 */
public interface RequestHandler {

    void handler(Request pack, ChannelHandlerContext context) throws HandlerException;
}
