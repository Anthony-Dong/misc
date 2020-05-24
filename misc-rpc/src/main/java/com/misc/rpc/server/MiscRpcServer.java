package com.misc.rpc.server;

import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyNode;
import com.misc.core.netty.NettyServer;
import com.misc.core.proto.ProtocolType;
import com.misc.core.proto.misc.MiscCodecProvider;
import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.core.register.RegistryService;
import com.misc.core.register.RemoteInfo;
import com.misc.core.test.EchoService;
import com.misc.core.test.User;
import com.misc.core.util.NetUtils;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import com.misc.rpc.proto.MiscServerConvertHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * misc协议
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscRpcServer extends NettyServer.Builder<MiscPack, MiscPack, RpcRequest, RpcResponse> {
    private RpcServerConfig rpcServerConfig;
    private MiscProperties properties;

    private MiscRpcServer(RpcServerConfig rpcServerConfig, MiscProperties properties) {
        this.rpcServerConfig = rpcServerConfig;
        this.properties = properties == null ? new MiscProperties() : properties;
    }

    protected void init() {
        properties.initServer(this);
        // 协议， misc / http
        super.setNettyCodecProvider(new MiscCodecProvider(properties));

        // misc -rpc / http-》rpc
        super.setNettyConvertHandler(new MiscServerConvertHandler(rpcServerConfig));

        // 真正的处理
        super.setNettyEventListener(new RpcServerHandler());
    }


    public static void runSync(RegistryService service, MiscProperties properties, RpcServerConfig rpcServerConfig) throws Throwable {
        NettyServer<MiscPack, MiscPack, RpcRequest, RpcResponse> server = new MiscRpcServer(rpcServerConfig, properties).build().start();
        RemoteInfo remoteInfo = new RemoteInfo();
        remoteInfo.setProtocolType(ProtocolType.MISC_PROTO);
        InetSocketAddress address = server.getAddress();
        remoteInfo.setHost(NetUtils.getIpByHost(address.getHostName()));
        remoteInfo.setPort(address.getPort());
        service.register(remoteInfo);
        server.sync();
    }

    public static void runSync(RegistryService service, RpcServerConfig rpcServerConfig) throws Throwable {
        runSync(service, null, rpcServerConfig);
    }
}
