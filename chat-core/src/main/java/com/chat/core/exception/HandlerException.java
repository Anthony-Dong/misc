package com.chat.core.exception;


/**
 * 全部 Netty Handler 异常
 * 主要负责与 {@link com.chat.core.handler.ChatEventHandler}
 *
 * @date:2019/12/26 20:28
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class HandlerException extends Exception {

    public HandlerException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public HandlerException() {
    }

    public HandlerException(Throwable cause) {
        super(cause);
    }


    public HandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
