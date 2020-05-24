package com.misc.core.register;

import com.misc.core.model.URL;
import com.misc.core.proto.ProtocolType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Getter
@Setter
public class RemoteInfo {

    private ProtocolType protocolType;

    private String host;

    private int port;

    private String serviceInterface;

    /**
     * 转换
     */
    public URL toUrl() {
        return new URL(protocolType.getInfo(), host, port);
    }

    /**
     * 转换
     */
    public static RemoteInfo makeInfo(URL url) {
        RemoteInfo remoteInfo = new RemoteInfo();
        remoteInfo.setHost(url.getHost());
        remoteInfo.setPort(url.getPort());
        remoteInfo.setServiceInterface(url.getServiceInterface());
        remoteInfo.setProtocolType(ProtocolType.MISC_PROTO);
        return remoteInfo;
    }
}
