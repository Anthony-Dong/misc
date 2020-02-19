package com.chat.server.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * 心跳检测的handler , 其实应该整到{@link ChatServerHandler} 但是由于部分原因没有改动
 *
 * @date:2019/11/16 17:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ChatServerHeartBeatHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.close();
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }
}
