package com.misc.rpc.proto;

import com.misc.core.model.MiscPack;
import com.misc.core.model.URL;
import com.misc.core.netty.NettyConvertHandler;
import com.misc.rpc.config.RpcConfig;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import io.netty.buffer.ByteBufAllocator;

/**
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscServerConvertHandler extends NettyConvertHandler<MiscPack, MiscPack, RpcRequest, RpcResponse> {
    private RpcConfig rpcConfig;

    public MiscServerConvertHandler(RpcConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
    }

    /**
     * 收到请求
     */
    @Override
    protected RpcRequest decode(MiscPack msg) {
        return rpcConfig.convertMiscPackToRpcRequest(msg);
    }

    /**
     * 发出响应
     */
    @Override
    protected MiscPack encode(ByteBufAllocator allocator, RpcResponse msg) {
        return rpcConfig.convertRpcResponseToMiscPack(msg);
    }
}
