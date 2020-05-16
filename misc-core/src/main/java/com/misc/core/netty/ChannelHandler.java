package com.misc.core.netty;

import com.misc.core.exception.HandlerException;
import com.misc.core.model.MiscMessage;
import io.netty.channel.Channel;

/**
 * 真正的处理器
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface ChannelHandler<IN extends MiscMessage, OUT extends MiscMessage> {

    /**
     * on channel connected.
     *
     * @param channel channel.
     */
    void connected(Channel channel) throws HandlerException;

    /**
     * on channel disconnected.
     *
     * @param channel channel.
     */
    void disconnected(Channel channel) throws HandlerException;

    /**
     * on message sent.
     *
     * @param channel channel.
     * @param message message.
     */
    void sent(Channel channel, OUT message) throws HandlerException;

    /**
     * on message received.
     *
     * @param channel channel.
     * @param message message.
     */
    void received(Channel channel, IN message) throws HandlerException;

    /**
     * on exception caught.
     *
     * @param channel   channel.
     * @param exception exception.
     */
    void caught(Channel channel, Throwable exception) throws HandlerException;


}
