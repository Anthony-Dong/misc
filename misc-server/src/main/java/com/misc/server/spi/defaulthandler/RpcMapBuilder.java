package com.misc.server.spi.defaulthandler;

import com.misc.server.rpc.RpcMap;

/**
 * @date:2020/2/18 20:34
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcMapBuilder {

    static final RpcMap map = new RpcMap();

    public static <T> void addService(Class<T> service, T proxy) {
        map.addService(service, proxy);
    }
}
