package com.chat.core.listener;

/**
 * @date:2019/11/16 18:52
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface ChatBootSource {

    /**
     *  启动成功
     *
     * @return
     */
    default boolean isSuccess() {
        return false;
    }

    /**
     *
     *  启动失败
     *
     * @return
     */
    default boolean isFailed() {
        return false;
    }

    /**
     * 一般是用于 客户端 , 如果关闭也要将 isFailed设置为 false
     * @return
     */
    default boolean isShutDown() {
        return false;
    }

    /**
     * 其他信息
     * @return
     */
    default Object hasOtherMsg() {
        return null;
    }

}
