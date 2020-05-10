package com.misc.server;

import com.misc.core.proto.ProtocolType;
import com.misc.core.proto.SerializableType;
import com.misc.core.test.EchoService;
import com.misc.server.handler.DefaultMiscServerContext;
import com.misc.server.handler.MiscServerContext;
import com.misc.server.netty.MiscServer;
import com.misc.server.spi.defaulthandler.RpcMapBuilder;


/**
 * 日志设置系统属性 {user.dir}
 */
public class ServerBoot {

    public static void main(String[] args) throws Exception {

        RpcMapBuilder.addService(EchoService.class, String::hashCode);

        MiscServerContext context = new DefaultMiscServerContext();

        context.setProtocolType(ProtocolType.HTTP_PROTO);
        context.setSerializableType(SerializableType.JSON);
        context.setThreadPool(20);
        MiscServer.run(9999, context);
    }
}
