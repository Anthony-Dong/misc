package com.chat.client.hander;

import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEventType;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理器配置类
 *
 * @date:2019/12/24 19:51
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ClientChatHandlerConstant {

    public static final Map<ChatEventType, ChatEventHandler> HANDLER_MAP = new HashMap<>();

    static {
        // 读
        HANDLER_MAP.put(ChatEventType.CLIENT_READ, new ClientReadChatEventHandler());

        // 关闭
        HANDLER_MAP.put(ChatEventType.CLIENT_SHUTDOWN, new ClientShutDownChatEventHandler());

        // 启动
        HANDLER_MAP.put(ChatEventType.CLIENT_START, new ClientStartChatEventHandler());

        // 连接成功
        HANDLER_MAP.put(ChatEventType.CLIENT_CONNECTED, new ClientConnectedChatEventHandler());
    }
}
