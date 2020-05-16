package com.misc.core.context;

import com.misc.core.exception.ContextException;
import com.misc.core.model.MiscResponse;
import com.misc.core.register.RemoteInfo;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class AbstractServerContext extends AbstractContext {

    private static final long serialVersionUID = -2178888052932011562L;


    private final Map<RemoteInfo, Channel> clientMap = new ConcurrentHashMap<>();


    protected Object config;


    public void addChannel(Channel channel) {
        RemoteInfo remoteInfo = new RemoteInfo();
        remoteInfo.setAddress(channel.remoteAddress());
        clientMap.put(remoteInfo, channel);
    }


    public void removeChannel(Channel channel) {

    }


    public void handlerMiscResponse(MiscResponse response) throws ContextException {

    }

    ;
}
