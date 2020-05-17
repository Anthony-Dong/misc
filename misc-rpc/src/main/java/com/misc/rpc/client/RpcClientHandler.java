package com.misc.rpc.client;

import com.misc.core.exception.HandlerException;
import com.misc.core.exception.TimeOutException;
import com.misc.core.netty.NettyEventListener;
import com.misc.rpc.core.*;
import io.netty.channel.Channel;

import java.lang.reflect.Method;

/**
 * todo
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcClientHandler implements NettyEventListener<RpcResponse, RpcRequest> {

    private FallBack fallBack=new FallBack() {
        @Override
        public void fallback(RpcResponse response) {

        }
    };

    @Override
    public void connected(Channel channel) throws HandlerException {

    }

    @Override
    public void disconnected(Channel channel) throws HandlerException {

    }

    @Override
    public void sent(Channel channel, RpcRequest message) throws HandlerException {

    }

    @Override
    public void received(Channel channel, RpcResponse message) throws HandlerException {
        RpcFuture.received(message, fallBack);
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws HandlerException {

    }

}
