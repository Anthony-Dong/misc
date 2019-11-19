package com.chat.server.core;

import com.chat.core.listener.ChatBootEvent;
import com.chat.core.listener.ChatBootListener;
import com.chat.core.listener.ChatBootSource;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * 心跳检测的handler ,
 *
 * @date:2019/11/16 17:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ChatServerHeartBeatHandler extends ChannelDuplexHandler {


    private ChatBootListener listener;

    public ChatServerHeartBeatHandler(ChatBootListener listener) {

        this.listener = listener;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {

            listener.onChatBootEvent(new ChatBootEvent(new ChatBootSource() {
                @Override
                public String hasOtherMsg() {
                    return ctx.channel().remoteAddress().toString();
                }
            }));

            // TODO: 2019/11/16   心跳检测服务器端  超时直接关闭 , 此时执行了  handlerRemoved() 方法
            ctx.close();

        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
