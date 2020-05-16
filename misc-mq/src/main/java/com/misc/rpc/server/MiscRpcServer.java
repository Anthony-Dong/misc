package com.misc.rpc.server;

import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyServer;
import com.misc.core.proto.misc.MiscCodecProvider;
import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.rpc.config.RpcConfig;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import com.misc.rpc.core.RpcServerHandler;
import com.misc.rpc.proto.MiscServerConvertHandler;

/**
 * misc协议
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscRpcServer extends NettyServer.Builder<MiscPack, MiscPack, RpcRequest, RpcResponse> {
    private RpcConfig rpcConfig;
    private MiscProperties properties;

    private MiscRpcServer(RpcConfig rpcConfig, MiscProperties properties) {
        this.rpcConfig = rpcConfig;
        this.properties = properties;
        init();
    }

    protected void init() {
        // 协议， misc / http
        super.setNettyCodecProvider(new MiscCodecProvider(properties));

        // misc -rpc / http-》rpc
        super.setNettyConvertHandler(new MiscServerConvertHandler(rpcConfig));

        // 真正的处理
        super.setNettyEventListener(new RpcServerHandler());
    }


    public static void run(MiscProperties properties, RpcConfig rpcConfig) throws Throwable {
        new MiscRpcServer(rpcConfig, properties).build().start().sync();
    }
}
