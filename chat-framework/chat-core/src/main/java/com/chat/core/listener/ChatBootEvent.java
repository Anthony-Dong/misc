package com.chat.core.listener;

import java.util.EventObject;

/**
 * 启动的事件
 *
 * @date:2019/11/11 15:33
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ChatBootEvent extends EventObject {

    /**
     * OBJ 的好处是可以随意定义
     *
     * @param source
     */
    public ChatBootEvent(ChatBootSource source) {
        super(source);
    }


    /**
     * 服务端启动成功 / 失败 /关闭
     */
    public static final ChatBootSource SERVER_SUCCESS = new ChatBootSource() {
        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailed() {
            return false;
        }
        @Override
        public boolean isShutDown() {
            return false;
        }
        @Override
        public Object hasOtherMsg() {
            return null;
        }
    };

    public static final ChatBootSource SERVER_FAILURE = new ChatBootSource() {
        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailed() {
            return true;
        }
        @Override
        public boolean isShutDown() {
            return false;
        }

        @Override
        public Object hasOtherMsg() {
            return null;
        }
    };



    /**
     * 客户端启动成功 / 失败 / 关闭
     */
    public static final ChatBootSource CLIENT_SUCCESS = new ChatBootSource() {
        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailed() {
            return false;
        }

        @Override
        public boolean isShutDown() {
            return false;
        }

        @Override
        public Object hasOtherMsg() {
            return null;
        }
    };

    public static final ChatBootSource CLIENT_FAILURE = new ChatBootSource() {
        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailed() {
            return true;
        }

        @Override
        public boolean isShutDown() {
            return false;
        }
        @Override
        public Object hasOtherMsg() {
            return null;
        }
    };


    public static final ChatBootSource CLIENT_SHUTDOWN = new ChatBootSource() {
        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailed() {
            return false;
        }

        @Override
        public boolean isShutDown() {
            return true;
        }
        @Override
        public Object hasOtherMsg() {
            return null;
        }
    };

    public static final ChatBootSource SERVER_SHUTDOWN = new ChatBootSource() {
        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailed() {
            return false;
        }

        @Override
        public boolean isShutDown() {
            return true;
        }
        @Override
        public Object hasOtherMsg() {
            return null;
        }
    };
}
