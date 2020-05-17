package com.misc.rpc.client;

import com.misc.rpc.core.RpcResponse;

/**
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface FallBack {

    /**
     * 当请求超时的响应都会在这里
     */
    void fallback(RpcResponse response);
}
