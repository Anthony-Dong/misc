package com.misc.core.netty;

import com.misc.core.model.MiscRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscPromise extends DefaultChannelPromise {


    public MiscPromise(Channel channel) {
        super(channel);
    }

    @Override
    public boolean trySuccess() {
        return false;
    }
}
