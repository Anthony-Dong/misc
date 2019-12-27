package com.chat.core.exception;

/**
 * 上下文异常 - > {@link HandlerException} 会将他抛出去
 *
 * @date:2019/12/27 9:44
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ContextException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ContextException(String message) {
        super(message);
    }

}
