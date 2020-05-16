package com.misc.rpc.core;

import com.misc.core.exception.HandlerException;
import com.misc.core.netty.NettyEventListener;
import io.netty.channel.Channel;

/**
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcServerHandler implements NettyEventListener<RpcRequest, RpcResponse> {

    @Override
    public void connected(Channel channel) throws HandlerException {

    }

    @Override
    public void disconnected(Channel channel) throws HandlerException {

    }

    @Override
    public void sent(Channel channel, RpcResponse message) throws HandlerException {

    }

    @Override
    public void received(Channel channel, RpcRequest message) throws HandlerException {

    }

    @Override
    public void caught(Channel channel, Throwable exception) throws HandlerException {

    }
}
