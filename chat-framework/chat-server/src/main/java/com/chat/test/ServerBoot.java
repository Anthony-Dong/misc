package com.chat.test;

import com.chat.server.handler.ChatServerContext;
import com.chat.server.netty.ChatServer;


/**
 * 日志设置系统属性 {user.dir}
 *
 * @date:2019/12/24 17:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerBoot {

    public static void main(String[] args) throws Exception {
        // 主线程阻塞
        ChatServer.run(9999, new ChatServerContext() {
            // 不重写任何方法.
        });
    }
}
