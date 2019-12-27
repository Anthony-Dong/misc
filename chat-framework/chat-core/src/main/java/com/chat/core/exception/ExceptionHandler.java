package com.chat.core.exception;

import com.chat.core.annotation.NotNull;

/**
 * 异常处理器, 我们为了节省对象 采用线程安全的 StringBuffer 对象, 对字符串进行拼接
 *
 * @date:2019/12/27 12:02
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class ExceptionHandler {

    private static final StringBuffer buffer = new StringBuffer(100);

    private static final String delimiter = "  ";

    /**
     *
     * @return 返回异常信息 类名+异常
     */
    public static <T> String makeError(@NotNull Class<T> clazz, @NotNull String msg) {
        buffer.setLength(0);
        buffer.append(clazz.getName()).append(delimiter).append(msg);
        return buffer.toString();
    }


    /**
     *
     * @return 实例化新的HandlerException
     */
    public static <T> HandlerException createHandlerException(@NotNull Class<T> clazz, @NotNull String msg) {
        String error = makeError(clazz, msg);
        return new HandlerException(error);
    }
}
