package com.chat.server.handler;

import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventType;
import com.chat.core.model.NPack;
import com.chat.server.netty.ChatServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * {@link ChatServerHandler#channelRead0(io.netty.channel.ChannelHandlerContext, com.chat.core.model.NPack)} 使用
 *
 * {@link ChatEventType#SERVER_READ} 类型
 *
 * @date:2019/12/24 20:44
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ServerReadChatEventHandler implements ChatEventHandler {
    private final Logger logger = LoggerFactory.getLogger(ServerReadChatEventHandler.class);

    @Override
    public void handler(ChatEvent event) {
        Object event1 = event.event();
        if (event1 instanceof NPack) {
            NPack pack = (NPack) event1;
            logger.info("[服务器] 接收信息成功 : {}." , pack);
        }
    }
}
