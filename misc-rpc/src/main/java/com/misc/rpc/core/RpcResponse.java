package com.misc.rpc.core;

import com.misc.core.model.Releasable;
import com.misc.core.model.URL;
import com.misc.core.proto.TypeConstants;
import com.misc.core.serialization.Deserializer;
import com.misc.core.serialization.SerializationFactory;
import com.misc.core.serialization.Serializer;
import com.misc.core.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 响应结果
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@ToString
@Getter
@Setter
public class RpcResponse implements Releasable {
    private static final Logger logger = LoggerFactory.getLogger(RpcResponse.class);

    /**
     * 协议
     */
    private String protocol;

    /**
     * 类型
     */
    private String type = TypeConstants.RPC_TYPE;

    /**
     * 当前服务器的host
     */
    private String host;

    /**
     * 当前服务器的port
     */
    private int port;

    /**
     * 响应需要接收方反序列化
     */
    private transient Deserializer<Object, Class<?>> deserializer;

    /**
     * 响应需要接收方反序列化
     */
    private transient Serializer<Object> serializer;

    /**
     * 响应结果
     */
    private Object result;


    /**
     * 唯一的响应id ,为包装，是为了包装是够设置没有
     */
    private String key;


    /**
     * 响应结果, 最好是根据接口的返回类型设置
     */
    private Class<?> resultType;


    /**
     * 响应结果
     */
    private RpcProperties properties;


    public void setProperty(String key, String value) {
        checkInit();
        properties.setProperties(key, value);
    }

    public void setProperties(Map<String, String> properties) {
        checkInit();
        this.properties.putAll(properties);
    }

    /**
     * 检测是否初始化
     */
    private synchronized void checkInit() {
        if (properties == null) {
            properties = new RpcProperties("");
        }
    }

    @Override
    public void release() {

    }


    public URL toURL() {
        if (StringUtils.isEmpty(type)) {
            throw new RuntimeException("can not handler type");
        }

        setProperty(URL.Constants.TYPE_KEY, type);
        if (type.equals(TypeConstants.HEART_TYPE)) {
            return new URL(protocol, host, port, properties);
        }


        if (key != null) {
            setProperty(URL.Constants.KEY_KEY, key);
        }
        if (result == null) {
            return new URL(protocol, host, port, properties);
        }
        // 序列化
        if (serializer == null || deserializer == null) {
            serializer = SerializationFactory.DEFAULT_OBJECT_SERIALIZATION;
            deserializer = SerializationFactory.DEFAULT_OBJECT_SERIALIZATION;
        }
        setProperty(URL.Constants.SERIALIZER_KEY, serializer.getClass().getName());
        setProperty(URL.Constants.DESERIALIZER_KEY, deserializer.getClass().getName());

        // return type
        if (resultType == null) {
            resultType = result.getClass();
            logger.warn("RpcResponse not set resultType , {} will set type {}",result, resultType);
        }
        setProperty(URL.Constants.RETURN_KEY, resultType.getName());
        return new URL(protocol, host, port, properties);
    }

    private static Class<?> getAccessClass(Class<?> superClazz) {
           return null;
    }
}
