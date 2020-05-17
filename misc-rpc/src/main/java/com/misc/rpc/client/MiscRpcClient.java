package com.misc.rpc.client;

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
    protected void init() {
        super.init();
        setNettyCodecProvider(new MiscCodecProvider(properties));
        setNettyConvertHandler(new MiscClientConvertHandler());
        setNettyEventListener(new RpcClientHandler());
    }


    private MiscRpcClient(MiscProperties properties) {
        this.properties = properties;
    }


    @SuppressWarnings("all")
    public static NettyClient<MiscPack, MiscPack, RpcResponse, RpcRequest> run() throws Throwable {
        MiscRpcClient miscRpcClient = new MiscRpcClient(null);
        NettyClient<MiscPack, MiscPack, RpcResponse, RpcRequest> build = miscRpcClient.build();
        return (NettyClient<MiscPack, MiscPack, RpcResponse, RpcRequest>) build.start();
    }


    public static void main(String[] args) throws Throwable {
        NettyClient<MiscPack, MiscPack, RpcResponse, RpcRequest> run = run();
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInvokeClazz(EchoService.class);
        HashMap<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("1", "2");
        rpcRequest.setParams(new Object[]{
                objectObjectHashMap
        });
        rpcRequest.setKey("1");
        rpcRequest.setInvokeMethod(EchoService.class.getMethod("getUses", HashMap.class));
        run.getChannel().writeAndFlush(rpcRequest).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("success");
            }
        });
        run.sync();
    }
}
