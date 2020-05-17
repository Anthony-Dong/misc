package com.misc.rpc.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.misc.core.exception.RpcException;
import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyClient;
import com.misc.core.test.EchoService;
import com.misc.rpc.core.*;
import io.netty.channel.Channel;

import java.lang.reflect.Method;
import java.util.stream.IntStream;

/**
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ReferenceBean<T> implements RpcInvokeHandler {
    private Class<T> reference;
    NettyClient<MiscPack, MiscPack, RpcResponse, RpcRequest> client;

    /**
     * write Channel；
     */
    private Channel channel;
    private KeyGenerator keyGenerator;


    public ReferenceBean(Class<T> reference) {
        this.reference = reference;
    }

    public T get() throws RpcException {
        // channel
        try {
            this.channel = MiscRpcClient.run().getChannel();
        } catch (Throwable throwable) {
            throw new RpcException(throwable.getMessage());
        }
        // 默认生成的
        if (keyGenerator == null) {
            keyGenerator = KeyGenerator.DEFAULT_KEY_GENERATOR;
        }
        // bean 获取
        return RpcProxy.newInstance(reference, this);
    }

    @Override
    public Object invoke(Class<?> clazz, Method method, Object... args) throws RpcException {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInvokeClazz(clazz);
        rpcRequest.setInvokeMethod(method);
        rpcRequest.setParams(args);
        rpcRequest.setKey(keyGenerator.getKey());
        channel.writeAndFlush(rpcRequest).addListener(future -> {
            if (future.cause() != null) {
                throw new RpcException("发送错误");
            }
        });
        return new RpcFuture(rpcRequest).get(1000).getResult();
    }

    public static void main(String[] args) throws NoSuchMethodException {

        EchoService echoService = new ReferenceBean<>(EchoService.class).get();
        long start = System.currentTimeMillis();
        IntStream.range(0, 10000).forEach(value -> {
            int hash = echoService.hash("111" + value);
                System.out.println(hash);
        });

        System.out.println(System.currentTimeMillis()-start);
    }


    public boolean[] get2() {
        return new boolean[]{};
    }
}
