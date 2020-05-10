package com.misc.server.handler;

import com.misc.core.exception.HandlerException;
import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEvent;
import com.misc.core.listener.MiscEventType;
import com.misc.core.model.MiscPack;
import com.misc.server.netty.MiscServerHandler;
import com.misc.server.spi.HandlerReceivePackage;

/**
 * {@link MiscServerHandler} 使用
 * <p>
 * {@link MiscEventType#SERVER_READ} 类型
 *
 * @date:2019/12/24 20:44
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ServerReadMiscEventHandler implements MiscEventHandler {
    private final HandlerReceivePackage handler;

    ServerReadMiscEventHandler(MiscServerContext context) {
        this.handler = new HandlerReceivePackage(context);
    }

    @Override
    public void handler(MiscEvent event) throws HandlerException {
        Object event1 = event.event();
        MiscPack pack = (MiscPack) event1;
        handler.handlerNPack(pack);
    }
}
