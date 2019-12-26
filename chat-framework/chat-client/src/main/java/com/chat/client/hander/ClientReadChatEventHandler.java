package com.chat.client.hander;

import com.chat.client.netty.ChantClientHandler;
import com.chat.core.exception.HandlerException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.model.NPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * {@link ChantClientHandler#channelRead0(io.netty.channel.ChannelHandlerContext, com.chat.core.model.NPack)} 方法处理器
 *
 * @date:2019/12/24 19:51
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientReadChatEventHandler implements ChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientReadChatEventHandler.class);

    private final ChatClientContext chatClientContext;

    ClientReadChatEventHandler(ChatClientContext chatClientContext) {
        this.chatClientContext = chatClientContext;
    }

    @Override
    public void handler(ChatEvent event) throws HandlerException {
        Object obj = event.event();
        if (obj instanceof NPack) {
            NPack nPack = (NPack) obj;
            if (null != chatClientContext) {
                chatClientContext.onReading(nPack);
                logger.info("[客户端] 接收到信息 : {}.", nPack);
            }
        }
    }
}
