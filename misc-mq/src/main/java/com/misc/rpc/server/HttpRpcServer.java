package com.misc.rpc.server;

import com.misc.core.netty.NettyServer;
import com.misc.core.proto.http.HttpCodecProvider;
import com.misc.core.proto.misc.MiscCodecProvider;
import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.rpc.config.RpcConfig;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import com.misc.rpc.core.RpcServerHandler;
import com.misc.rpc.proto.HttpServerConvertHandler;
import com.misc.rpc.proto.MiscServerConvertHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * http
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class HttpRpcServer extends NettyServer.Builder<FullHttpRequest, FullHttpResponse, RpcRequest, RpcResponse> {

    private RpcConfig rpcConfig;

    private HttpRpcServer(RpcConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
    }


    protected void init() {
        super.setNettyCodecProvider(new HttpCodecProvider());
        super.setNettyConvertHandler(new HttpServerConvertHandler(rpcConfig));
        super.setNettyEventListener(new RpcServerHandler());
    }


    public static void run(RpcConfig rpcConfig) throws Throwable {
        new HttpRpcServer(rpcConfig).build().start().sync();
    }

}
