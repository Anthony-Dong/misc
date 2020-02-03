package com.chat.core.exception;

/**
 * TODO
 *
 * @date:2020/1/21 15:52
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class RegisterException extends RuntimeException{


    public RegisterException(String message) {
        super(message);
    }


    public RegisterException(String message, Throwable cause) {
        super(message, cause);
    }
}
