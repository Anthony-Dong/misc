package com.misc.spring;


import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.core.register.ZKRegistryService;
import com.misc.core.test.EchoService;
import com.misc.core.test.User;
import com.misc.rpc.server.RpcServerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static com.misc.rpc.server.MiscRpcServer.run;


public class ServerApp {
    public static void main(String[] args) {
        RpcServerConfig config = new RpcServerConfig();
        config.addInvoker(EchoService.class, new EchoService() {
            @Override
            public int[] hash(String str) {
                return new int[]{str.hashCode()};
            }

            @Override
            public List<User> getUses(HashMap<String, String> value) {
                value.forEach((s, s2) -> System.out.println(s + ":" + s2));
                ArrayList<User> users = new ArrayList<>();
                users.add(new User("111"));
                return users;
            }
        });
        try {
            run(new ZKRegistryService(), new MiscProperties(), config);
        } catch (Throwable throwable) {
            System.out.println(throwable.getMessage());
        }
    }
}
