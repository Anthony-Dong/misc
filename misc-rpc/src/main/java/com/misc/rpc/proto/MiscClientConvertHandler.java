package com.misc.rpc.proto;

import com.misc.core.exception.ConvertException;
import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyConvertHandler;
import com.misc.core.serialization.SerializationFactory;
import com.misc.rpc.config.RpcConvertUtil;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import io.netty.buffer.ByteBufAllocator;

/**
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscClientConvertHandler extends NettyConvertHandler<MiscPack, MiscPack, RpcResponse, RpcRequest> {

    private static final SerializationFactory FACTORY = new SerializationFactory();

    /**
     * 收到响应结果
     */
    @Override
    protected RpcResponse decode(MiscPack msg) throws ConvertException {
        RpcResponse response = RpcConvertUtil.convertMiscPackToRpcResponse(msg, FACTORY);
        msg.release();
        return response;
    }

    /**
     * 发出去的是请求
     */
    @Override
    protected MiscPack encode(ByteBufAllocator allocator, RpcRequest msg) throws ConvertException {
        try {
            return RpcConvertUtil.convertRpcRequestToMiscPack(msg);
        } catch (Exception e) {
            throw e;
        }
    }
}
