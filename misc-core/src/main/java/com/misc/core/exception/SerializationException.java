package com.misc.core.exception;

/**
 * 序列化异常
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class SerializationException extends RuntimeException {

    private static final long serialVersionUID = -2734233562552761766L;


    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
