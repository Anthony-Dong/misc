package com.chat.conf.exception;

/**
 * TODO
 *
 * @date:2020/2/19 16:01
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class ChatHttpRequestException extends RuntimeException {

    private static final long serialVersionUID = -5846843825749206275L;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ChatHttpRequestException(String message) {
        super(message);
    }
}
