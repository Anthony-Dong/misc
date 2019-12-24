package com.chat.server.handler;

import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEventType;

import java.util.HashMap;
import java.util.Map;

/**
 * 模式匹配. 因为java中switch无法使用枚举类型
 * @date:2019/12/24 20:45
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ServerChatHandlerConstant {

    public static final Map<ChatEventType, ChatEventHandler> SERVER_MAP = new HashMap<>();

    static {
        SERVER_MAP.put(ChatEventType.SERVER_READ, new ServerReadChatEventHandler());
        SERVER_MAP.put(ChatEventType.SERVER_START, new ServerStartChatEventHandler());
        SERVER_MAP.put(ChatEventType.SERVER_SHUTDOWN, new ServerShutdownChatEventHandler());
        SERVER_MAP.put(ChatEventType.SERVER_CHANNEL_REGISTERED, new ServerChannelRegisteredChatEventHandler());
        SERVER_MAP.put(ChatEventType.SERVER_HANDLER_REMOVED, new ServerHandlerRemovedChatEventHandler());
    }
}
