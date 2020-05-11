package com.misc.server.rpc;

import com.misc.core.model.netty.Arg;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * rpc的调用信息
 *
 * @date: 2020-05-11
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Invocation<T> {


    /**
     * 接口类
     */
    private Class<T> inter;

    /**
     * 目标实现
     */
    private T target;

    /**
     * 方法信息
     */
    private Method method;

    /**
     * 方法参数信息
     */
    private Arg[] params;


    // todo 元信息初始化
}
