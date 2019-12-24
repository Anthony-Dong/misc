package com.chat.client.hander;

import com.chat.client.netty.ChantClientHandler;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.model.NPack;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端连接服务器端成功
 * <p>
 * {@link ChantClientHandler#channelActive(io.netty.channel.ChannelHandlerContext)} 处理器
 *
 * @date:2019/12/24 19:46
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientConnectedChatEventHandler implements ChatEventHandler {
    private final Logger logger = LoggerFactory.getLogger(ClientReadChatEventHandler.class);

    @Override
    public void handler(ChatEvent event) {
        Object object = event.event();
        if (object instanceof ChannelHandlerContext) {
            ChannelHandlerContext context = (ChannelHandlerContext) object;

            NPack nPack = new NPack("/test", "测试");

            context.writeAndFlush(nPack);

            logger.info("[客户端] 注册成功 , 发送信息 : {}.", nPack);
        }
    }
}
