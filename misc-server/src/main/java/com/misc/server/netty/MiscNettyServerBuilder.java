package com.misc.server.netty;


import com.misc.core.proto.misc.common.MiscProperties;
import com.misc.core.model.MiscPack;
import com.misc.core.netty.NettyCodecProvider;
import com.misc.core.netty.NettyServer;
import com.misc.core.proto.misc.MiscCodecProvider;

/**
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscNettyServerBuilder<ChannelInBound, ChannelOutBound> extends NettyServer.Builder<MiscPack, MiscPack, ChannelInBound, ChannelOutBound> {

    public MiscNettyServerBuilder(MiscProperties properties) {
        super.setNettyCodecProvider(new MiscCodecProvider(properties));
    }

    @Override
    public NettyServer<MiscPack, MiscPack, ChannelInBound, ChannelOutBound> build() {
        return super.build();
    }
}
