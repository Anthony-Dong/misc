package com.misc.core.proto;

import com.misc.core.env.MiscProperties;
import com.misc.core.proto.http.HttpCodec;
import com.misc.core.proto.misc.MiscCodecHandler;
import com.misc.core.proto.misc.serial.MiscSerializableHandler;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.Map;

/**
 * todo
 *
 * @date: 2020-05-10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ProtocolAdapter {

    public ChannelHandler[] getHandler(ProtocolType protocolType, MiscProperties properties, Map<Byte, MiscSerializableHandler> serializeHandlerMap) {
        switch (protocolType) {
            case MISC_PROTO:
                return new ChannelHandler[]{new MiscCodecHandler(properties, serializeHandlerMap)};
            case HTTP_PROTO:
                return new ChannelHandler[]{new HttpServerCodec(), new HttpObjectAggregator(1024), new HttpCodec(properties,serializeHandlerMap)};
            default:
                throw new RuntimeException("其他协议还没有");
        }
    }
}
