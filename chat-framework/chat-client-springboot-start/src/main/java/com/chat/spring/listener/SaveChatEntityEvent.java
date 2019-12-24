package com.chat.spring.listener;

import com.chat.core.model.ChatEntity;
import org.springframework.context.ApplicationEvent;

/**
 *
 *
 * @date:2019/11/13 17:02
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class SaveChatEntityEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public SaveChatEntityEvent(ChatEntity source) {
        super(source);
    }
}
