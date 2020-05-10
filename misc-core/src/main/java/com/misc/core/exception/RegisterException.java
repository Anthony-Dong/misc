package com.misc.core.exception;

/**
 *  注册异常
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

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public RegisterException(Throwable cause) {
        super(cause);
    }
}
