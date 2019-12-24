package com.chat.client.netty;

import com.chat.core.listener.ChatEventListener;
import com.chat.core.netty.Constants;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 客户端心跳检测
 *
 * @date:2019/11/16 17:44
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ClientHeartBeatHandler extends ChannelDuplexHandler {

    private ChatEventListener listener;

    public ClientHeartBeatHandler(ChatEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 从下往上遍历
            ctx.channel().writeAndFlush(Constants.HEART_BEAT_NPACK);
        } else {
            // 交给父类处理
            super.userEventTriggered(ctx, evt);
        }
    }
}
