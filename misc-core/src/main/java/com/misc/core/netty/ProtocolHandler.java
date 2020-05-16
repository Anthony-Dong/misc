package com.misc.core.netty;

import com.misc.core.model.MiscMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * 这里处理协议
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@ChannelHandler.Sharable
public abstract class ProtocolHandler extends ChannelDuplexHandler {

    /**
     * read
     */
    protected abstract Object decode(Object msg);

    /**
     * write
     */
    protected abstract Object encode(Object msg);

    /**
     * 写出去的是请求
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof MiscMessage) {
            super.write(ctx, encode(msg), promise);
        } else {
            promise.setFailure(new RuntimeException("类型不支持"));
        }
    }

    /**
     * 编码 -> misc-request
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, decode(msg));
    }


    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        // 这里统一都写出去
        super.flush(ctx);
    }
}
