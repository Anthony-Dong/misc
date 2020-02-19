package com.chat.core.exception;


/**
 * 代理异常
 *
 * @date:2020/2/18 19:59
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class ProxyException extends HandlerException {

    public ProxyException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public ProxyException() {
    }

    public ProxyException(Throwable cause) {
        super(cause);
    }
}
