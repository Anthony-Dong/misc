package com.misc.rpc.proto;

import com.misc.core.exception.ConvertException;
import com.misc.core.netty.NettyConvertHandler;
import com.misc.rpc.server.RpcServerConfig;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * 序列化
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class HttpServerConvertHandler extends NettyConvertHandler<FullHttpRequest, FullHttpResponse, RpcRequest, RpcResponse> {

    private RpcServerConfig rpcServerConfig;

    public HttpServerConvertHandler(RpcServerConfig rpcServerConfig) {
        this.rpcServerConfig = rpcServerConfig;
    }

    @Override
    protected RpcRequest decode(FullHttpRequest msg)throws ConvertException {
        return rpcServerConfig.convertFullHttpRequestToRpcRequest(msg);
    }

    @Override
    protected FullHttpResponse encode(ByteBufAllocator allocator, RpcResponse msg)throws ConvertException {
        return rpcServerConfig.convertRpcResponseToFullHttpResponse(msg);
    }
}
