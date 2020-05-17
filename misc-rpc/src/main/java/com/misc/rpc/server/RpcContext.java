package com.misc.rpc.server;

import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;

/**
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface RpcContext {
    void handler(RpcRequest request, RpcResponse response);
}
