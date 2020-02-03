package com.chat.server.handler;

import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEventType;

import java.util.HashMap;
import java.util.Map;

/**
 * 模式匹配. 因为java中switch无法使用枚举类型
 *
 * @date:2019/12/24 20:45
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerChatHandlerConstant {
    private final Map<ChatEventType, ChatEventHandler> handlerMap;

    public ServerChatHandlerConstant(ChatServerContext context) {
        this.handlerMap = new HashMap<>();
        this.handlerMap.put(ChatEventType.SERVER_READ, new ServerReadChatEventHandler(context));
        this.handlerMap.put(ChatEventType.SERVER_START, new ServerStartChatEventHandler(context));
        this.handlerMap.put(ChatEventType.SERVER_SHUTDOWN, new ServerShutdownChatEventHandler(context));
        this.handlerMap.put(ChatEventType.SERVER_CHANNEL_REGISTERED, new ServerChannelRegisteredChatEventHandler(context));
        this.handlerMap.put(ChatEventType.SERVER_HANDLER_REMOVED, new ServerHandlerRemovedChatEventHandler(context));
    }

    public Map<ChatEventType, ChatEventHandler> getHandlerMap() {
        return handlerMap;
    }
}
