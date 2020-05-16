package com.misc.core.netty;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface NettyNode {

    /**
     * 启动Netty
     */
    NettyNode start() throws Throwable;

    /**
     * 关闭Netty
     */
    NettyNode close() throws Throwable;

    /**
     * sync的作用就是阻塞，直到netty 服务器关闭
     */
    NettyNode sync() throws Throwable;
}
