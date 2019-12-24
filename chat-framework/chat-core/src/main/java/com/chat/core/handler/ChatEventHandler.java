package com.chat.core.handler;

import com.chat.core.listener.ChatEvent;
import com.chat.core.listener.ChatEventType;

/**
 * 我们的事件处理器 {@link ChatEvent} {@link ChatEventType}
 *
 * @date:2019/12/24 19:46
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public interface ChatEventHandler {

    void handler(ChatEvent event);
}
