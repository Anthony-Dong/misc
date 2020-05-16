package com.misc.core.http;

import com.misc.core.netty.NettyConvertHandler;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * http的转换器
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class HttpNettyConvertHandler<ChannelInBound, ChannelOutBound> extends NettyConvertHandler<FullHttpRequest, FullHttpResponse, ChannelInBound, ChannelOutBound> {

    @Override
    protected ChannelInBound decode(FullHttpRequest msg) {
        return null;
    }

    @Override
    protected FullHttpResponse encode(ByteBufAllocator allocator, ChannelOutBound msg) {
        return null;
    }
}
