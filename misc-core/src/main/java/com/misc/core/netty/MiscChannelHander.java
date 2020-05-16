package com.misc.core.netty;

import com.misc.core.exception.HandlerException;
import io.netty.channel.Channel;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscChannelHander implements ChannelHandler {

    @Override
    public void connected(Channel channel) throws HandlerException {

    }

    @Override
    public void disconnected(Channel channel) throws HandlerException {

    }

    @Override
    public void sent(Channel channel, Object message) throws HandlerException {

    }

    @Override
    public void received(Channel channel, Object message) throws HandlerException {

    }

    @Override
    public void caught(Channel channel, Throwable exception) throws HandlerException {

    }
}
