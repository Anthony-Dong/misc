package com.misc.rpc.client;

import com.misc.core.commons.Constants;
import com.misc.core.exception.RpcException;
import com.misc.core.exception.TimeOutException;
import com.misc.core.loadbalance.LoadBalance;
import com.misc.core.proto.ProtocolType;
import com.misc.core.proto.TypeConstants;
import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.core.register.RegistryService;
import com.misc.core.register.RemoteInfo;
import com.misc.rpc.core.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.*;

/**
 * 客户端调用的主要实现
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ReferenceBean<T> implements RpcInvokeHandler {
    private static final Logger logger = LoggerFactory.getLogger(ReferenceBean.class);
    private Class<T> reference;
    /**
     * write Channel；
     */
    private List<Channel> channels = new ArrayList<>();
    /**
     * 生成器
     */
    private KeyGenerator keyGenerator;

    /**
     * 注册中心，服务发现中心
     */
    private RegistryService registryService;


    /**
     * 负载均衡器
     */
    private LoadBalance<Channel> loadBalance;


    /**
     * 方法的参数
     */
    private Map<Method, RpcProperties> map = new HashMap<>();


    public ReferenceBean(Class<T> reference) {
        this.reference = reference;
    }

    public T get() throws RpcException {
        /**
         * 注册中心
         */
        Set<RemoteInfo> lookup = registryService.lookup(new RemoteInfo());

        for (RemoteInfo remoteInfo : lookup) {
            MiscProperties miscProperties = new MiscProperties();
            miscProperties.setHost(remoteInfo.getHost());
            miscProperties.setPort(remoteInfo.getPort());
            try {
                Channel channel = MiscRpcClient.run(miscProperties).getChannel();
                this.channels.add(channel);
            } catch (Throwable throwable) {
                throw new RpcException(throwable);
            }
        }
        // 默认生成的
        if (keyGenerator == null) {
            keyGenerator = KeyGenerator.DEFAULT_KEY_GENERATOR;
        }

        // 没有设置默认使用随机
        if (loadBalance == null) {
            loadBalance = DEFAULT_LOAD_BALANCE;
        }

        // bean 获取
        return RpcProxy.newInstance(reference, this);
    }

    @Override
    public Object invoke(Class<?> clazz, Method method, Object... args) throws RpcException {
        return RpcInvoke(clazz, method, args);
    }

    private Object RpcInvoke(Class<?> clazz, Method method, Object[] args) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setProtocol(ProtocolType.MISC_PROTO.getInfo());
        rpcRequest.setType(TypeConstants.RPC_TYPE);
        rpcRequest.setInvokeClazz(clazz);
        rpcRequest.setInvokeMethod(method);
        rpcRequest.setParams(args);
        rpcRequest.setKey(keyGenerator.getKey());
        RpcProperties properties = map.get(method);
        long timeout;
        if (properties != null) {
            timeout = properties.getTimeOut();
            rpcRequest.setProperties(properties);
        } else {
            timeout = Constants.DEFAULT_REQUEST_TIMEOUT;
        }

        Channel channel = loadBalance.loadBalance(channels);
        // channel 如果设置了fallback则直接调用为空本地调用
        if (channel == null) {
            if (assertFallBack(properties)) {
                try {
                    return fallBack(method, properties, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RpcException(String.format("ReferenceBean invoke fall back object error :%s", e.getMessage()));
                }
            }
            throw new RpcException("ReferenceBean invoke fail because null available server");
        }

        // 设置地址
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        rpcRequest.setHost(address.getHostName());
        rpcRequest.setPort(address.getPort());
        channel.writeAndFlush(rpcRequest).addListener(future -> {
            // 异常判断
            if (future.cause() == null) {
                return;
            }
            // 如果关闭了channel ，那么直接移除，不依赖于监听移除，做法简单
            if (future.cause() instanceof ClosedChannelException) {
                channels.remove(channel);
                logger.warn("ReferenceBean remove server {} because disconnected", channel.remoteAddress());
            }
        });

        // 获取结果超时
        try {
            return new RpcFuture(rpcRequest).get(timeout).getResult();
        } catch (TimeOutException e) {
            if (assertFallBack(properties)) {
                try {
                    logger.warn("rpc invoke exception will run fullback ,err:{}", e.getMessage());
                    return fallBack(method, properties, args);
                } catch (IllegalAccessException | InvocationTargetException e1) {
                    throw e;
                }
            }
            throw e;
        } finally {
            // 释放掉
            rpcRequest.release();
        }
    }

    private Object fallBack(Method method, RpcProperties properties, Object[] args) throws IllegalAccessException, InvocationTargetException {
        Object back = properties.getFallBack();
        return method.invoke(back, args);
    }

    private boolean assertFallBack(RpcProperties properties) {
        return properties != null && properties.getFallBackClass() != null;
    }

    /**
     * 设置方法属性
     */
    public void setMethodPropertie(RpcProperties properties) {
        RpcProperties rpcProperties = map.get(Objects.requireNonNull(properties.getMethod()));
        if (rpcProperties == null) {
            map.put(Objects.requireNonNull(properties.getMethod()), properties);
        } else {
            rpcProperties.putAll(properties);
        }
    }

    public RegistryService getRegistryService() {
        return registryService;
    }

    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }


    private static final LoadBalance<Channel> DEFAULT_LOAD_BALANCE = new LoadBalance<Channel>() {
        private Random random = new Random();

        @Override
        public Channel loadBalance(List<Channel> list) {
            int size = list.size();
            if (size == 0) {
                return null;
            }
            int i = random.nextInt(size);
            return list.get(i);
        }
    };
}
