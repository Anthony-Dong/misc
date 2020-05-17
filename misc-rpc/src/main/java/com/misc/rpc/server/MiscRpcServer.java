package com.misc.rpc.server;

import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyServer;
import com.misc.core.proto.misc.MiscCodecProvider;
import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.core.test.EchoService;
import com.misc.core.test.User;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import com.misc.rpc.proto.MiscServerConvertHandler;

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
        this.properties = properties;
        init();
    }

    protected void init() {
        // 协议， misc / http
        super.setNettyCodecProvider(new MiscCodecProvider(properties));

        // misc -rpc / http-》rpc
        super.setNettyConvertHandler(new MiscServerConvertHandler(rpcServerConfig));

        // 真正的处理
        super.setNettyEventListener(new RpcServerHandler());
    }


    public static void run(MiscProperties properties, RpcServerConfig rpcServerConfig) throws Throwable {
        new MiscRpcServer(rpcServerConfig, properties).build().start().sync();
    }


    public static void main(String[] args) {
        RpcServerConfig config = new RpcServerConfig();
        config.addInvoker(EchoService.class, new EchoService() {
            @Override
            public int hash(String str) {
                return 0;
            }

            @Override
            public List<User> getUses(HashMap<String, String> value) {
                value.forEach(new BiConsumer<String, String>() {
                    @Override
                    public void accept(String s, String s2) {
                        System.out.println(s + ":" + s2);
                    }
                });
                ArrayList<User> users = new ArrayList<>();
                users.add(new User("111"));
                return users;
            }
        });
        try {
            run(null, config);
        } catch (Throwable throwable) {
            System.out.println(throwable.getMessage());
        }
    }
}
