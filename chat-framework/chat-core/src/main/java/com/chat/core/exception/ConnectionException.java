package com.chat.core.exception;

import java.io.IOException;

/**
 * TODO
 *
 * @date:2019/11/11 11:56
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ConnectionException extends IOException {


    /**
     * Constructs an {@code IOException} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of {@code cause}).
     * This constructor is useful for IO exceptions that are little more
     * than wrappers for other throwables.
     *
     * @param cause The cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A null value is permitted,
     *              and indicates that the cause is nonexistent or unknown.)
     * @since 1.6
     */
    public ConnectionException(Throwable cause) {
        super(cause);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code IOException} with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    public ConnectionException(String message) {
        super(message);
    }
}
