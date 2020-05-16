package com.misc.core.exception;


import com.misc.core.handler.MiscEventHandler;

/**
 * 处理器异常
 * 主要负责与 {@link MiscEventHandler}
 *
 * @date:2019/12/26 20:28
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class HandlerException extends RuntimeException {

    private static final long serialVersionUID = -603746667483767491L;

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
