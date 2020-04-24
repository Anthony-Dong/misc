package com.chat.core.listener;

import com.chat.core.handler.ChatEventHandler;

/**
 * 启动的事件  ,其实我应该自己实现一个EventObject
 * {@link ChatEventHandler} 处理器
 *
 * @date:2019/11/11 15:33
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface ChatEvent {

    String NULL = "NULL MESSAGE ! ";

    /**
     * {@link ChatEventType} This is the type of HandlerType .
     * {@link ChatEventHandler} This is the handler of front type .
     *
     * @return ChatEventType
     */
    ChatEventType eventType();

    /**
     * OTHER MESSAGE
     *
     * @return Object This the obj because of no declare type .
     */
    default Object event() {
        return NULL;
    }


    /**
     * 服务端启动事件
     */

    ChatEvent SERVER_START = new ChatEvent() {
        @Override
        public ChatEventType eventType() {
            return ChatEventType.SERVER_START;
        }
    };

    /**
     * 服务器关闭事件
     */
    ChatEvent SERVER_SHUTDOWN = new ChatEvent() {
        @Override
        public ChatEventType eventType() {
            return ChatEventType.SERVER_SHUTDOWN;
        }
    };


    /**
     * 客户端启动事件
     */
    ChatEvent CLIENT_START = new ChatEvent() {
        @Override
        public ChatEventType eventType() {
            return ChatEventType.CLIENT_START;
        }
    };


    /**
     * 客户端关闭事件
     */
    ChatEvent CLIENT_SHUTDOWN = new ChatEvent() {
        @Override
        public ChatEventType eventType() {
            return ChatEventType.CLIENT_SHUTDOWN;
        }
    };

}
