package com.misc.rpc;


import com.misc.core.register.ZKRegistryService;
import com.misc.core.test.EchoService;
import com.misc.rpc.server.MiscRpcServer;
import com.misc.rpc.server.RpcServerConfig;

/**
 * 服务器 最好新人开启debug日志模式
 */
public class ServerApp {
    public static void main(String[] args) throws Throwable {
        // 设置一个rpc config
        RpcServerConfig config = new RpcServerConfig();
        config.addInvoker(EchoService.class, (EchoService) str -> new int[]{str.hashCode()});

        // 注册中心
        ZKRegistryService registry = new ZKRegistryService("localhost:2181");

        // 启动,同步阻塞
        MiscRpcServer.runSync(registry, config);
    }
}
