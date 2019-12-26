package com.chat.server.context;

import com.chat.server.handler.ChatServerContext;
import io.netty.channel.ChannelHandlerContext;

/**
 * @date:2019/12/26 17:26
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class DefaultChatServerContext extends ChatServerContext {
    /**
     * 注意  ChannelHandlerContext ,每一个客户端连接都会有一个 ChannelHandlerContext
     * 你可以自己去维护
     *
     * @param context ChannelHandlerContext
     */
    @Override
    protected void onRemove(ChannelHandlerContext context) {

    }

    /**
     * 注意  ChannelHandlerContext ,每一个客户端连接都会有一个 ChannelHandlerContext
     * 你可以自己去维护
     *
     * @param context ChannelHandlerContext
     */
    @Override
    protected void onRegister(ChannelHandlerContext context) {

    }
}
