package com.chat.core.exception;

/**
 * {@link com.chat.core.util.Assert} 异常
 *
 * @date:2019/12/26 21:19
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public final class HandlerNullPointerException extends RuntimeException {

    public static final HandlerNullPointerException NULL = new HandlerNullPointerException("Assert 不能为空");

    public HandlerNullPointerException(String message) {
        super(message);
    }
}
