package com.misc.client.hander;

import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEventType;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理器配置类
 *
 * @date:2019/12/24 19:51
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ClientChatHandlerConstant {

    private final Map<MiscEventType, MiscEventHandler> HANDLER_MAP = new HashMap<>();

    public ClientChatHandlerConstant(MiscClientContext miscClientContext) {
        // 读
        HANDLER_MAP.put(MiscEventType.CLIENT_READ, new ClientReadMiscEventHandler(miscClientContext));

        // 关闭
        HANDLER_MAP.put(MiscEventType.CLIENT_SHUTDOWN, new ClientShutDownMiscEventHandler(miscClientContext));

        // 启动
        HANDLER_MAP.put(MiscEventType.CLIENT_START, new ClientStartMiscEventHandler(miscClientContext));

        // 连接成功
        HANDLER_MAP.put(MiscEventType.CLIENT_CONNECTED, new ClientConnectedMiscEventHandler(miscClientContext));
    }

    public Map<MiscEventType, MiscEventHandler> getHandlerMap() {
        return HANDLER_MAP;
    }
}
