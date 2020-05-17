package com.misc.rpc.core;


import com.misc.core.exception.ConvertException;
import com.misc.core.exception.RpcException;
import com.misc.core.model.Releasable;
import com.misc.core.model.URL;
import com.misc.core.proto.TypeConstants;
import com.misc.core.serialization.Deserializer;
import com.misc.core.serialization.Serializer;
import com.misc.rpc.config.RpcConvertUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.Map;

import static com.misc.core.model.URL.Constants.*;

import static com.misc.core.serialization.SerializationFactory.DEFAULT_SERIALIZATION;

/**
 * Rpc调用的参数
 * <p>
 * 1、请求某个方法以 method=method1
 * 2、需要指定反序列化的序列化的类
 * <p>
 * 3、以方法为单位
 * 4、方法的属性（超时：以methodname.k1  mname.k2）
 * 5、需要告诉请求的接口类
 * <p>
 * 6、需要告诉调用的方法参数 -> 放在请求行中 -> 存入到属性中， 比如参数 methodname.params=1,2,3
 * 7、需要告诉调用的方法参数类型
 * <p>
 * - >业务层面 拿到的对象是已经确定好的 ........ 需要在 convert层 做全部的类型转换，不应该放到业务层
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Getter
@Setter
@ToString
public class RpcRequest implements Releasable {

    private static final long serialVersionUID = -3967657981609594301L;

    /**
     * type
     */
    private String type = TypeConstants.RPC_TYPE;

    /**
     * 接口名称
     */
    private Class<?> invokeClazz;

    /**
     * 调用对象（服务端才有这个对象）
     */
    private Object invokeTarget;

    /**
     * 调用的方法
     */
    private Method invokeMethod;

    /**
     * 方法类型
     */
    private Class<?>[] ParamsType;

    /**
     * 方法参数
     */
    private Object[] Params;

    /**
     * 当前客户端/服务器的地址
     */
    private String host;

    /**
     * 端口
     */
    private int port;

    /**
     * 协议类型
     */
    private String protocol;

    /**
     * 方法的属性
     */
    private RpcProperties properties;

    /**
     * 序列化方式
     */
    private Serializer<Object[]> serializer;

    /**
     * 反序列化方式
     */
    private Deserializer<Object[], Class<?>[]> deserializer;


    /**
     * 唯一值 ， 客户端的唯一值
     */
    private String key;

    /**
     * 是否同步 ， 默认同步
     */
    private boolean isSync = true;

    /**
     * 是否需要ack ， 默认需要
     */
    private boolean needAck = true;

    /**
     * 添加method属性
     */
    public void setMethodProperty(String key, String value) {
        checkInit();
        properties.setMethodProperties(key, value);
    }

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
        if (invokeMethod == null) {
            throw new NullPointerException("RpcRequest.method can not be null");
        }
        if (properties == null) {
            properties = new RpcProperties(invokeMethod.getName());
        }
    }

    @Override
    public void release() {
        invokeClazz = null;
        invokeTarget = null;
        invokeMethod = null;
        ParamsType = null;
        Params = null;
        serializer = null;
        deserializer = null;
        properties.clear();
        properties = null;
    }


    /**
     * 导出
     */
    public URL toURL() throws ConvertException {
        // 检查类型
        if (!TypeConstants.validateType(type)) {
            throw new RuntimeException("can not handler type");
        }
        // 其次是设置属性
        setProperty(TYPE_KEY, type);
        if (type.equals(TypeConstants.HEART_TYPE)) {
            return new URL(protocol, host, port, properties);
        }

        // 不是rpc类型
        if (invokeClazz == null) {
            throw new ConvertException(String.format("RpcRequest invokeClazz can not be NULL, %s", this));
        }

        // 是rpc 没有设置method
        if (invokeMethod == null) {
            throw new ConvertException(String.format("RpcRequest.invokeMethod can not be NULL, %s", this));
        }
        ParamsType = invokeMethod.getParameterTypes();

        // 没有设置序列化
        if (serializer == null || deserializer == null) {
            serializer = DEFAULT_SERIALIZATION;
            deserializer = DEFAULT_SERIALIZATION;
        }

        // 设置方法属性
        if (ParamsType != null && ParamsType.length > 0) {
            setMethodProperty(PARAMS_KEY, RpcConvertUtil.convertMethodParamsTypeToString(ParamsType));
        }

        if (needAck) {
            if (key == null) {
                throw new ConvertException(String.format("RpcRequest need ack but not set key, %s",this));
            }
            setProperty(ACK_KEY, needAck ? "1" : "0");
            setProperty(KEY_KEY, key);
        }

        // 设置主要属性
        setProperty(METHOD_KEY, invokeMethod.getName());
        setProperty(SERIALIZER_KEY, serializer.getClass().getName());
        setProperty(DESERIALIZER_KEY, deserializer.getClass().getName());
        return new URL(protocol, host, port, invokeClazz.getName(), properties);
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParamsType(Class<?>[] paramsType) {
        ParamsType = paramsType;
    }

    /**
     * 最简单的构造器
     */
    public RpcRequest(Class<?> invokeClazz, Method invokeMethod, Object[] params, String host, int port, RpcProperties properties) {
        this.invokeClazz = invokeClazz;
        this.invokeMethod = invokeMethod;
        Params = params;
        this.host = host;
        this.port = port;
        this.properties = properties;
    }

    public RpcRequest() {
    }
}
