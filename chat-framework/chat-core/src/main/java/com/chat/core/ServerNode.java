package com.chat.core;

/**
 * 服务节点的父类 ,init 就已经不使用 了 ,推荐使用构造方法
 */
public interface ServerNode {

    /**
     * 服务初始化
     */
    @Deprecated
    default void init() throws Exception {
        // no
    }

    /**
     * 服务启动
     *
     * @throws Exception 所有的异常都不归我们去管理
     */
    void start() throws Exception;

    /**
     * 服务关闭
     *
     * @throws Exception 所有的异常都不归我们去管理
     */
    void shutDown() throws Exception;

}
