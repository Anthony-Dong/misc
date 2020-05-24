package com.misc.core.commons;

/**
 * 一些属性常量
 *
 * @date:2020/2/18 17:41
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class PropertiesConstant {

    /**
     * 版本号
     */
    public static final String CLIENT_SERVER_VERSION = "misc.server.version";

    /**
     * 协议类型
     */
    public static final String CLIENT_PROTOCOL_TYPE = "misc.protocol.type";


    /**
     * 序列化类型
     */
    public static final String CLIENT_SERIALIZABLE_TYPE = "misc.serializable.type";

    /**
     * port
     */
    public static final String CLIENT_PORT = "misc.port";

    /**
     * host
     */
    public static final String CLIENT_HOST = "misc.host";


    /**
     * 文件系统需要保存的位置地址
     */
    public static final String CLIENT_FILE_DIR = "misc.file.dir";


    /**
     * 文件系统需要保存的位置地址
     */
    public static final String CLIENT_LOG_DIR = "misc.log.dir";


    /**
     * ms ， 连接超时时间
     */
    public static final String CLIENT_CONNECT_TIMEOUT = "client.connect.timeout";

    /**
     * ms ， 请求超时时间
     */
    public static final String CLIENT_REQUEST_TIMEOUT = "misc.request.timeout";

    /**
     * s ， 客户端心跳时间
     */
    public static final String CLIENT_HEART_INTERVAL = "misc.client.heart.interval";

    /**
     * s , 服务器心跳时间
     */
    public static final String SERVER_HEART_INTERVAL = "misc.server.heart.interval";


    public static final String CLIENT_REGISTER_REDIS_HOST = "client.register.redis.host";

    public static final String CLIENT_REGISTER_REDIS_PORT = "client.register.redis.port";

    public static final String CLIENT_REGISTER_ZK_URL = "client.register.zk.url";


    /**
     * ms
     */
    public static final String CLIENT_REGISTER_HEART_TIME = "client.register.heart.time";


    public static final String CLIENT_REGISTER_KEY = "register-node";


    public static final String CLIENT_THREAD_QUEUE_SIZE = "client.thread.queue.size";
    public static final String CLIENT_THREAD_POOL_SIZE = "client.thread.pool.size";
    public static final String CLIENT_THREAD_POOL_NAME = "client.thread.pool.name";

    public static final String SERVER_THREAD_POOL_NAME = "server.thread.pool.name";
    public static final String SERVER_THREAD_QUEUE_SIZE = "server.thread.queue.size";
    public static final String SERVER_THREAD_POOL_SIZE = "server.thread.pool.size";
}
