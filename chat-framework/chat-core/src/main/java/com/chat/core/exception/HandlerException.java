package com.chat.core.exception;

/**
 * 全部 Netty Handler 异常
 * 主要负责与 {@link com.chat.core.handler.ChatEventHandler}
 *
 * @date:2019/12/26 20:28
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class HandlerException extends Exception {

    public HandlerException(String message) {
        super(message);
    }


    public static void main(String[] args) {

        try {
            get();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    static void get() throws Exception {
        get2();
    }


    static void get2() throws HandlerException{
        try {
            int x = 1 / 0;
        } catch (Exception e) {
            throw new HandlerException("异常");
        }
    }

}
