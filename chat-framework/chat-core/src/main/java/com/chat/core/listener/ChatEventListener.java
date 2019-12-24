package com.chat.core.listener;

import java.util.EventListener;

/**
 * 聊天的监听器 - > 主要是用在客户端和服务器端中
 * <p>
 * 主要是将启动 成功/失败后的操作交给了用户 , 这个很好地解决了我们失败重试的操作
 *
 * @date:2019/11/11 15:32
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public interface ChatEventListener extends EventListener {
    void onChatEvent(ChatEvent event) throws Exception;
}
