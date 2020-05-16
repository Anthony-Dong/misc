package com.misc.core;

import com.misc.core.model.MiscMessage;

/**
 * Server节点
 *
 * @date: 2020-05-10 01:21
 * @author：<a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface MiscNode {
    /**
     * 初始化 init
     *
     * @throws Exception
     */
    default void init() throws Exception {

    }

    /**
     * 启动
     *
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 关闭
     *
     * @throws Exception
     */
    void stop() throws Exception;
}
