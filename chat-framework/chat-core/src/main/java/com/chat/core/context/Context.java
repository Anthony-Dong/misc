package com.chat.core.context;

/**
 * 上下文接口
 *
 * @date:2020/2/18 11:49
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface Context {

    void onBootstrap();

    void onShutdown();

}
