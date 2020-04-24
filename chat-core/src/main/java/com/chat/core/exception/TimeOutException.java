package com.chat.core.exception;

/**
 * 超时异常
 *
 * @date:2020/2/17 16:06
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class TimeOutException extends HandlerException {

    public TimeOutException(String message) {
        super(message);
    }


    public TimeOutException(Throwable cause) {
        super(cause);
    }
}
