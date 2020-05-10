package com.misc.core.context;

/**
 * 上下文接口
 *
 * @date:2020/2/18 11:49
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface Context {

    /**
     * 当上下午启动
     */
    void onBootstrap();


    /**
     * 当上下文关闭
     */
    void onShutdown();

}
