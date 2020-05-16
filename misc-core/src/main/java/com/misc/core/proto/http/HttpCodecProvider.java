package com.misc.core.proto.http;

import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyCodecProvider;
import com.misc.core.proto.misc.MiscPackageDecoder;
import com.misc.core.proto.misc.MiscPackageEncoder;
import com.misc.core.proto.misc.common.MiscProperties;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * misc 协议的 解码器
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class HttpCodecProvider implements NettyCodecProvider<FullHttpRequest, FullHttpResponse> {

    public HttpCodecProvider() {
    }

    @Override
    public ChannelHandler[] get() {
        return null;
    }
}