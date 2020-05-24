package com.misc.core.proto.misc.common;

import com.misc.core.commons.PropertiesConstant;
import com.misc.core.netty.NettyClient;
import com.misc.core.netty.NettyServer;
import com.misc.core.util.StringUtils;

import static com.misc.core.commons.PropertiesConstant.*;

/**
 * 一个ENV对象
 *
 * @date:2020/2/24 20:15
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscProperties extends TypeProperties {

    private static final long serialVersionUID = 5619866598364075132L;

    public void setVersion(short version) {
        setShort(PropertiesConstant.CLIENT_SERVER_VERSION, version);
    }

    public short getVersion() {
        return getShortProperty(CLIENT_SERVER_VERSION);
    }

    /**
     * {@link com.misc.core.proto.misc.common.MiscSerializableType}
     */
    public void setSerialType(MiscSerializableType type) {
        setByte(PropertiesConstant.CLIENT_SERIALIZABLE_TYPE, type.getCode());
    }

    public MiscSerializableType getSerialType() {
        String property = getProperty(PropertiesConstant.CLIENT_SERIALIZABLE_TYPE);
        if (StringUtils.isEmpty(property)) {
            return null;
        }
        return MiscSerializableType.getType(Byte.valueOf(property));
    }

    public String getHost() {
        return getProperty(CLIENT_HOST);
    }

    public void setHost(String host) {
        setString(CLIENT_HOST, host);
    }

    public int getPort() {
        return getIntProperty(CLIENT_PORT);
    }

    public void setPort(int port) {
        setInt(CLIENT_PORT, port);
    }


    /**
     * 初始化服务器
     */
    public void initServer(NettyServer.Builder builder) {
        builder.setPort(getPort());
        builder.setHost(getHost());
        builder.setThreadPoolSize(getIntProperty(SERVER_THREAD_POOL_SIZE));
        builder.setThreadQueueSize(getIntProperty(SERVER_THREAD_QUEUE_SIZE));
        builder.setThreadName(getProperty(SERVER_THREAD_POOL_NAME));
        builder.setHeartInterval(getIntProperty(SERVER_HEART_INTERVAL));
    }

    /**
     * 初始化客户端
     */
    public void initClient(NettyClient.Builder builder) {
        builder.setPort(getPort());
        builder.setHost(getHost());
        builder.setConnectTimeout(getIntProperty(CLIENT_CONNECT_TIMEOUT));
        builder.setThreadName(getProperty(CLIENT_THREAD_POOL_NAME));
        builder.setThreadPoolSize(getIntProperty(CLIENT_THREAD_POOL_SIZE));
        builder.setThreadQueueSize(getIntProperty(CLIENT_THREAD_QUEUE_SIZE));
        builder.setHeartInterval(getIntProperty(CLIENT_HEART_INTERVAL));
    }

    public MiscProperties() {
        super();
    }

    public MiscProperties(String host, int port) {
        super();
        setPort(port);
        setHost(host);
    }

    public MiscProperties(int port) {
        super();
        setPort(port);
    }
}
