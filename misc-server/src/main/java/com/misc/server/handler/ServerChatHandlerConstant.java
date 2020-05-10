package com.misc.server.handler;

import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEventType;

import java.util.HashMap;
import java.util.Map;

/**
 * 模式匹配. 因为java中switch无法使用枚举类型
 *
 * @date:2019/12/24 20:45
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerChatHandlerConstant {
    private final Map<MiscEventType, MiscEventHandler> handlerMap;

    public ServerChatHandlerConstant(MiscServerContext context) {
        this.handlerMap = new HashMap<>();
        this.handlerMap.put(MiscEventType.SERVER_READ, new ServerReadMiscEventHandler(context));
        this.handlerMap.put(MiscEventType.SERVER_START, new ServerStartMiscEventHandler(context));
        this.handlerMap.put(MiscEventType.SERVER_SHUTDOWN, new ServerShutdownMiscEventHandler(context));
        this.handlerMap.put(MiscEventType.SERVER_CHANNEL_REGISTERED, new ServerChannelRegisteredMiscEventHandler(context));
        this.handlerMap.put(MiscEventType.SERVER_HANDLER_REMOVED, new ServerHandlerRemovedMiscEventHandler(context));
    }

    public Map<MiscEventType, MiscEventHandler> getHandlerMap() {
        return handlerMap;
    }
}
