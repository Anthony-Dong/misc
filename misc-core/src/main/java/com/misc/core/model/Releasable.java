package com.misc.core.model;

/**
 * release 释放引用（提早释放内存）
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface Releasable {

    void release();
}
