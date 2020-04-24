package com.chat.core;

/**
 * 服务节点的父类 ,init 就已经不使用 了 ,推荐使用构造方法
 */
public abstract class ServerNode {
    /**
     * 服务初始化
     */
    protected void init() throws Exception {
       // 不一定实现
    }

    /**
     * 服务启动
     *
     * @throws Exception 所有的异常都不归我们去管理
     */
    protected abstract void start() throws Exception;

    /**
     * 服务关闭
     *
     * @throws Exception 所有的异常都不归我们去管理
     */
    protected abstract void shutDown() throws Exception;

}
