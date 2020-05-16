package com.misc.core.netty;

import io.netty.channel.ChannelHandler;

/**
 * 核心的编解码 - 处理器
 *
 * 这里设置 泛型 是为了约束
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface NettyCodecProvider<ProtoInBound, ProtoOutBound> {
    /**
     * 获取编解码器,需要注意{@link io.netty.channel.ChannelHandler.Sharable}
     */
    ChannelHandler[] get();
}
