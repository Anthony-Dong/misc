package com.misc.core.listener;

import com.misc.core.handler.MiscEventHandler;

/**
 * 启动的事件  ,其实我应该自己实现一个EventObject
 * {@link MiscEventHandler} 处理器
 *
 * @date:2019/11/11 15:33
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface MiscEvent {

    String NULL = "NULL MESSAGE ! ";

    /**
     * {@link MiscEventType} This is the type of HandlerType .
     * {@link MiscEventHandler} This is the handler of front type .
     *
     * @return MiscEventType
     */
    MiscEventType eventType();

    /**
     * OTHER MESSAGE
     *
     * @return Object This the obj because of no declare type .
     */
    default Object event() {
        return NULL;
    }

}
