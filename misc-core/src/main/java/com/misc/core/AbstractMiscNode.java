package com.misc.core;

import com.misc.core.env.MiscProperties;
import com.misc.core.exception.BootstrapException;
import com.misc.core.listener.MiscEvent;
import com.misc.core.listener.MiscEventListener;
import com.misc.core.proto.ProtocolType;
import com.misc.core.proto.SerializableType;
import com.misc.core.proto.misc.serial.*;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.misc.core.commons.Constants.DEFAULT_HOST;
import static com.misc.core.commons.Constants.DEFAULT_PORT;
import static com.misc.core.commons.PropertiesConstant.CLIENT_HOST;
import static com.misc.core.commons.PropertiesConstant.CLIENT_PORT;

/**
 * 公共的属性
 *
 * @date: 2020-05-10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class AbstractMiscNode implements MiscNode {

    /**
     * netty 绑定的 ip
     */
    protected final InetSocketAddress address;

    /**
     * 启动监听器
     * 可以看 {@link MiscEvent SERVER_SUCCESS} 属性去判断 失败/成功
     */
    protected final MiscEventListener listener;

    /**
     * 线程池
     */
    protected final Executor executor;

    /**
     * 属性
     */
    protected final MiscProperties properties;

    /**
     * 编解码处理器
     */
    protected final Map<Byte, MiscSerializableHandler> serializeHandlerMap = new HashMap<>();


    protected final ProtocolType protocolType;

    /**
     * 构造器
     *
     * @throws BootstrapException 初始化异常
     */
    protected AbstractMiscNode(InetSocketAddress address, MiscEventListener listener, Executor executor, MiscProperties properties, ProtocolType protocolType) {
        this.address = address;
        this.listener = listener;
        this.executor = executor;
        this.properties = properties;
        this.protocolType = protocolType;
        initSerializeHandleMap(this.serializeHandlerMap);
    }

    /**
     * 初始化 处理器
     */
    private static void initSerializeHandleMap(Map<Byte, MiscSerializableHandler> codecHandlerMap) {
//        if (codecHandlerMap == null) return;
//        Set<Byte> set = codecHandlerMap.keySet();
//        set.forEach(SerializableType::filterCodecType);

        // 初始化Map
        codecHandlerMap.put(SerializableType.MESSAGE_PACK.getCode(), new MessagePackSerializableType());
        codecHandlerMap.put(SerializableType.MESSGAE_PACK_GZIP.getCode(), new GzipMessagePackSerializableType());
        codecHandlerMap.put(SerializableType.JSON.getCode(), new JsonSerializableType());
        codecHandlerMap.put(SerializableType.Java_Serializable.getCode(), new ByteSerializableType());
    }

    /**
     * 处理器
     */
    protected static InetSocketAddress getAddress(MiscProperties properties) {
        return new InetSocketAddress(properties.getProperty(CLIENT_HOST, DEFAULT_HOST), properties.getInt(CLIENT_PORT, DEFAULT_PORT));
    }
}
