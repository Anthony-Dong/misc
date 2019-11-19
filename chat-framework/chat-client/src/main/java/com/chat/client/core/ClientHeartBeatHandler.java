package com.chat.client.core;

import com.chat.core.listener.ChatBootEvent;
import com.chat.core.listener.ChatBootListener;
import com.chat.core.packutil.Constants;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 客户端心跳检测
 *
 * @date:2019/11/16 17:44
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ClientHeartBeatHandler extends ChannelDuplexHandler {

    private ChatBootListener listener;

    public ClientHeartBeatHandler(ChatBootListener listener) {
        this.listener = listener;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 发送一个心跳包
            ctx.writeAndFlush(Constants.HEART_BEAT_NPACK).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (!future.isSuccess()) {
                        // TODO: 2019/11/16  失败 .. 发送一个关闭事件
                        listener.onChatBootEvent(new ChatBootEvent(ChatBootEvent.CLIENT_SHUTDOWN));
                    }
                }
            });
        } else {
            // 交给父类处理
            super.userEventTriggered(ctx, evt);
        }
    }
}
