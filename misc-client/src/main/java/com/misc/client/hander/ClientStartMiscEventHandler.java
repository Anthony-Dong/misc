package com.misc.client.hander;

import com.misc.core.exception.HandlerException;
import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEvent;

import java.net.InetSocketAddress;

/**
 * 客户端启动事件处理器
 * <p>
 *
 * @date:2019/12/24 19:49
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ClientStartMiscEventHandler implements MiscEventHandler {

    private final MiscClientContext miscClientContext;

    ClientStartMiscEventHandler(MiscClientContext miscClientContext) {
        this.miscClientContext = miscClientContext;
    }

    @Override
    public void handler(MiscEvent event) throws HandlerException {
        Object obj = event.event();
        if (obj instanceof InetSocketAddress) {
            if (null != miscClientContext) {
                miscClientContext.onBootstrap();
            }
        }
    }
}
