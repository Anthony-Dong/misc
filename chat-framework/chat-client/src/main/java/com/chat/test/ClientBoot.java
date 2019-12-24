package com.chat.test;

import com.chat.client.netty.ChatClient;
import com.chat.client.hander.ClientChatHandlerConstant;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEventType;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 *
 * @date:2019/12/24 17:22
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ClientBoot {

    public static void main(String[] args) {
        final Map<ChatEventType, ChatEventHandler> handlerMap = ClientChatHandlerConstant.HANDLER_MAP;

        ChatClient client = new ChatClient(new NioEventLoopGroup(1), new InetSocketAddress(8888), event -> {
            ChatEventHandler handler = handlerMap.get(event.eventType());
            handler.handler(event);
        });

        // 启动
        try {
            client.start();
        } catch (Exception e) {
            //
        }
    }
}
