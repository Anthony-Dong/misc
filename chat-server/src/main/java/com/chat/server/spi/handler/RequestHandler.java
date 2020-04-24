package com.chat.server.spi.handler;

import com.chat.core.exception.HandlerException;
import com.chat.core.model.netty.Request;
import io.netty.channel.ChannelHandlerContext;

/**
 * {@link RequestHandler} 处理器
 */
public interface RequestHandler {

    void handler(Request pack, ChannelHandlerContext context) throws HandlerException;
}
