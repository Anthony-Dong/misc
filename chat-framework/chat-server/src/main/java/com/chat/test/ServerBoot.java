package com.chat.test;

import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEventType;
import com.chat.server.netty.ChatServer;
import com.chat.server.handler.ServerChatHandlerConstant;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @date:2019/12/24 17:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerBoot {

    public static void main(String[] args) {
        final Map<ChatEventType, ChatEventHandler> serverMap = ServerChatHandlerConstant.SERVER_MAP;

        ChatServer chatServer = new ChatServer(new InetSocketAddress(8888), event -> {
            ChatEventHandler handler = serverMap.get(event.eventType());
            handler.handler(event);
        });

        try {
            chatServer.start();
        } catch (Exception ignored) {
            // ...
        }
    }
}
