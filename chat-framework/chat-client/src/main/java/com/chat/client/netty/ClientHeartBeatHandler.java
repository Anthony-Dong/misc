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

public final class ClientHeartBeatHandler extends ChannelDuplexHandler {

    /**
     * 其实么啥用 发送心跳包 , 我感觉没必要做监听
     */
    private final ChatEventListener listener;

    ClientHeartBeatHandler(ChatEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 从下往上遍历
            ctx.writeAndFlush(Constants.HEART_BEAT_NPACK);
        } else {
            // 交给其他处理
            ctx.fireUserEventTriggered(evt);
        }
    }
}
