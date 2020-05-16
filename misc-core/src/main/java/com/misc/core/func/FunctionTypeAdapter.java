package com.misc.core.func;

import com.misc.core.netty.NettyConvertHandler;
import com.misc.core.proto.ProtocolType;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class FunctionTypeAdapter {
    public NettyConvertHandler getProtocolHandler(ProtocolType protocolType, FunctionType functionType) {
        return new RpcServerNettyConvertHandler(protocolType);
    }
}
