package com.misc.core.proto.misc;

import com.misc.core.env.MiscProperties;
import com.misc.core.proto.misc.serial.MiscSerializableHandler;
import io.netty.channel.CombinedChannelDuplexHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 编解码器
 *
 * @date: 2020-05-10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscCodecHandler extends CombinedChannelDuplexHandler<PackageDecoder, PackageEncoder> {

    /**
     * 走的提供的属性
     *
     * @param properties      属性
     * @param serializeHandlerMap 编解码器
     */
    public MiscCodecHandler(MiscProperties properties, Map<Byte, MiscSerializableHandler> serializeHandlerMap) {
        init(new PackageDecoder(properties, serializeHandlerMap), new PackageEncoder(properties, serializeHandlerMap));
    }

    /**
     * 走的默认属性
     */
    public MiscCodecHandler() {
        this(new MiscProperties(), new HashMap<>());
    }
}
