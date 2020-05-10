package com.misc.core.handler;

import com.misc.core.exception.HandlerException;
import com.misc.core.listener.MiscEvent;
import com.misc.core.listener.MiscEventType;

/**
 * 我们的事件处理器 {@link MiscEvent} {@link MiscEventType}
 * <p>
 * 采用 策略（Strategy）模式
 * 来处理不同的事件类型
 *
 * @date:2019/12/24 19:46
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public interface MiscEventHandler {

    void handler(MiscEvent event) throws HandlerException;


    MiscEventHandler DEFAULT_MISC_EVENT_HANDLER = event -> {

    };
}
