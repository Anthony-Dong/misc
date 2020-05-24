package com.misc.rpc.server;

import com.misc.core.exception.HandlerException;
import com.misc.core.netty.NettyEventListener;
import com.misc.core.proto.TypeConstants;
import com.misc.rpc.core.RpcRequest;
import com.misc.rpc.core.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.internal.ConcurrentSet;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * rpc 处理器
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcServerHandler implements NettyEventListener<RpcRequest, RpcResponse> {
    private final Set<Channel> channels = new ConcurrentSet<>();

    @Override
    public void connected(Channel channel) throws HandlerException {
        channels.add(channel);
        logger.info("the client {} connected", channel.remoteAddress());
    }

    @Override
    public void disconnected(Channel channel) throws HandlerException {
        channels.remove(channel);
        logger.info("the client {} disconnected", channel.remoteAddress());
    }

    @Override
    public void sent(Channel channel, RpcResponse message) throws HandlerException {
    }

    @Override
    public void received(Channel channel, RpcRequest request) throws HandlerException {
        if (request.getType().equals(TypeConstants.RPC_TYPE)) {
            handlerRpcInvoker(channel, request);
        }
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws HandlerException {
        logger.error("happened exception {}", exception.getMessage());
    }

    @Override
    public void eventTriggered(Channel channel, Object event) throws HandlerException {
        if (event instanceof IdleStateEvent) {
            logger.info("close client {} because heart beat timeout", channel.remoteAddress());
            channel.close();
        }
    }

    /**
     * 获取连接数
     */
    public int getClientCount() {
        return channels.size();
    }

    private void handlerRpcInvoker(Channel channel, RpcRequest request) {
        try {
            Object invoke = request.getInvokeMethod().invoke(request.getInvokeTarget(), request.getParams());
            if (request.getProperties().needAck()) {
                // 设置响应结果
                RpcResponse response = new RpcResponse();
                response.setHost(request.getHost());
                response.setPort(request.getPort());
                response.setResult(invoke);
                response.setKey(request.getKey());
                // 清空request
                request.release();
                channel.writeAndFlush(response).addListener((ChannelFutureListener) future -> {
                    if (future.cause() != null) {
                        // 调用异常接口
                        RpcServerHandler.this.caught(channel, future.cause());
                    }
                });
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
