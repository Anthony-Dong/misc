package com.chat.client.exception;

import io.netty.channel.ChannelFuture;

/**
 * TODO
 *
 * @date:2019/11/19 16:30
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class WebChannelFutureException extends RuntimeException {


    private ChannelFuture future;


    public WebChannelFutureException(String message, ChannelFuture future) {
        super(message);
        this.future = future;
    }


    public WebChannelFutureException(String message, Throwable cause, ChannelFuture future) {
        super(message, cause);
        this.future = future;
    }

    public ChannelFuture getFuture() {
        return future;
    }

    public void setFuture(ChannelFuture future) {
        this.future = future;
    }
}
