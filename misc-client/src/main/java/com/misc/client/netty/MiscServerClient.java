package com.misc.client.netty;


import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyClient;
import com.misc.core.netty.NettyCodecProvider;
import com.misc.core.proto.misc.MiscCodecProvider;


/**
 * misc client
 *
 * @date:2019/11/10 11:35
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class MiscServerClient<ChannelInBound, ChannelOutBound> extends NettyClient.Builder<MiscPack, MiscPack, ChannelInBound, ChannelOutBound> {

    public MiscServerClient(MiscProperties properties) {
        super.setNettyCodecProvider(new MiscCodecProvider(properties));
    }
}