package com.misc.core.func;

import com.misc.core.netty.ProtocolHandler;
import com.misc.core.netty.rpc.RpcServerProtocolHandler;
import com.misc.core.proto.ProtocolType;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class FunctionTypeAdapter {
    public ProtocolHandler getProtocolHandler(ProtocolType protocolType, FunctionType functionType) {
        return new RpcServerProtocolHandler(protocolType);
    }
}
