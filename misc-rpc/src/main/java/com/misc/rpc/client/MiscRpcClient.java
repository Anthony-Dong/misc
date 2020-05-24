package com.misc.rpc.client;

import com.misc.core.commons.Constants;
import com.misc.core.commons.PropertiesConstant;
import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyClient;
import com.misc.core.proto.TypeConstants;
import com.misc.core.proto.misc.MiscCodecProvider;
import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.core.test.EchoService;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import com.misc.rpc.proto.MiscClientConvertHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * todo
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscRpcClient extends NettyClient.Builder<MiscPack, MiscPack, RpcResponse, RpcRequest> {

    private MiscProperties properties;

    @Override
    protected void init() throws RuntimeException {
        properties.initClient(this);
        setNettyCodecProvider(new MiscCodecProvider(properties));
        setNettyConvertHandler(new MiscClientConvertHandler());
        setNettyEventListener(new RpcClientHandler());
    }


    private MiscRpcClient(MiscProperties properties) {
        this.properties = properties == null ? new MiscProperties() : properties;
    }


    @SuppressWarnings("all")
    public static NettyClient<MiscPack, MiscPack, RpcResponse, RpcRequest> run(MiscProperties properties) throws Throwable {
        MiscRpcClient miscRpcClient = new MiscRpcClient(properties);
        NettyClient<MiscPack, MiscPack, RpcResponse, RpcRequest> build = miscRpcClient.build();
        return (NettyClient<MiscPack, MiscPack, RpcResponse, RpcRequest>) build.start();
    }
}
