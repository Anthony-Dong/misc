package com.misc.rpc.proto;

import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyConvertHandler;
import com.misc.rpc.server.RpcServerConfig;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import io.netty.buffer.ByteBufAllocator;

/**
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscServerConvertHandler extends NettyConvertHandler<MiscPack, MiscPack, RpcRequest, RpcResponse> {
    private RpcServerConfig rpcServerConfig;

    public MiscServerConvertHandler(RpcServerConfig rpcServerConfig) {
        this.rpcServerConfig = rpcServerConfig;
    }

    /**
     * 收到请求
     */
    @Override
    protected RpcRequest decode(MiscPack msg) {
        RpcRequest rpcRequest = rpcServerConfig.convertMiscPackToRpcRequest(msg);
        msg.release();
        return rpcRequest;
    }

    /**
     * 发出响应
     */
    @Override
    protected MiscPack encode(ByteBufAllocator allocator, RpcResponse msg) {
        MiscPack miscPack = rpcServerConfig.convertRpcResponseToMiscPack(msg);
        msg.release();
        return miscPack;
    }
}
