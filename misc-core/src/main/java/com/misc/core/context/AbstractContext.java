package com.misc.core.context;

import com.misc.core.proto.misc.common.MiscProperties;


import com.misc.core.proto.ProtocolType;
import com.misc.core.proto.misc.common.MiscSerializableType;
import com.misc.core.register.RegistryService;
import com.misc.core.util.ThreadPool;

import static com.misc.core.commons.PropertiesConstant.*;
import static com.misc.core.commons.Constants.*;


/**
 * 一个公用的上下文对象
 *
 * @date: 2020-05-10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class AbstractContext extends MiscProperties implements Context {

    private static final long serialVersionUID = 2927926566034015466L;
    /**
     * 上下文名称
     */
    protected String contextName = DEFAULT_CONTEXT_NAME;

    /**
     * host
     */
    protected String host = DEFAULT_HOST;
    /**
     * 端口
     */
    protected int port = DEFAULT_PORT;

    /**
     * 版本号
     */
    protected short version = DEFAULT_SERVER_VERSION;

    /**
     * 默认的心跳时间,使用的是服务器端的心跳时间
     */
    protected int heartInterval = DEFAULT_SERVER_HEART_INTERVAL;

    /**
     * 请求超时时间
     */
    protected long requestTimeout = DEFAULT_REQUEST_TIMEOUT;

    /**
     * 连接超时时间
     */
    protected long connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    /**
     * 线程池
     */
    protected ThreadPool threadPool = new ThreadPool(DEFAULT_THREAD_SIZE, DEFAULT_THREAD_QUEUE_SIZE, DEFAULT_THREAD_NAME);


    /**
     * 注册中心
     */
    protected RegistryService registryService;


    /**
     * 序列号类型{@link MiscSerializableType}
     */
    protected MiscSerializableType miscSerializableType = DEFAULT_SERIALIZABLE_TYPE;


    /**
     * 协议类型(目前就一个协议) {@link ProtocolType}
     */
    protected ProtocolType protocolType = DEFAULT_PROTOCOL_TYPE;


    public AbstractContext() {

    }


    public String getContextName() {
        return contextName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Short getVersion() {
        return version;
    }

    public int getHeartInterval() {
        return heartInterval;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public RegistryService getRegistryService() {
        return registryService;
    }

    public long getRequestTimeout() {
        return requestTimeout;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public void setHost(String host) {
        setProperty(CLIENT_HOST, host);
        this.host = host;
    }

    public void setPort(int port) {
        setInt(CLIENT_PORT, port);
        this.port = port;
    }

    public void setVersion(short version) {
        setShort(CLIENT_SERVER_VERSION, version);
        this.version = version;
    }

    public void setHeartInterval(int heartInterval) {
        setInt(SERVER_HEART_INTERVAL, heartInterval);
        this.heartInterval = heartInterval;
    }

    public void setThreadPool(int threadPoolSize, int queueSize, String threadName) {
        this.threadPool = new ThreadPool(threadPoolSize, queueSize, threadName);
    }

    public void setThreadPool(int threadPoolSize, String threadName) {
        setThreadPool(threadPoolSize, DEFAULT_THREAD_QUEUE_SIZE, threadName);
    }

    public void setThreadPool(int threadPoolSize) {
        setThreadPool(threadPoolSize, DEFAULT_THREAD_QUEUE_SIZE, DEFAULT_THREAD_NAME);
    }

    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }

    public void setRequestTimeout(long requestTimeout) {
        setLong(CLIENT_REQUEST_TIMEOUT, requestTimeout);
        this.requestTimeout = requestTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        setLong(CLIENT_CONNECT_TIMEOUT, connectTimeout);
        this.connectTimeout = connectTimeout;
    }

    public MiscSerializableType getMiscSerializableType() {
        return miscSerializableType;
    }

    public void setMiscSerializableType(MiscSerializableType miscSerializableType) {
        setByte(CLIENT_SERIALIZABLE_TYPE, miscSerializableType.getCode());
        this.miscSerializableType = miscSerializableType;
    }

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ProtocolType protocolType) {
        setByte(CLIENT_PROTOCOL_TYPE, protocolType.getType());
        this.protocolType = protocolType;
    }
}
