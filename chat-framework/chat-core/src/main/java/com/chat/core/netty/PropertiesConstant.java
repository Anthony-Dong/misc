package com.chat.core.netty;

/**
 * 一些属性常量
 *
 * @date:2020/2/18 17:41
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class PropertiesConstant {

    static final String CLIENT_VERSION = "client.proto.version";

    static final String CLIENT_THREAD_QUEUE_SIZE = "client.thread.queue.size";

    static final String CLIENT_THREAD_CORE_SIZE = "client.thread.core.size";

    static final String CLIENT_PORT = "client.port";

    static final String CLIENT_HOST = "client.host";

    static final String CLIENT_FILE_PROTOCOL = "client.file.version";

    static final String CLIENT_FILE_START = "client.file.start";

    static final String CLIENT_FILE_END = "client.file.end";
    static final String CLIENT_ID_END = "client.file.id";
    /**
     * ms
     */
    static final String CLIENT_TIME_OUT = "client.timeout";

    static final String CLIENT_HEART_INTERVAL = "client.heart.interval";

    public static final String CLIENT_REGISTER_REDIS_HOST = "client.register.redis.host";

    public static final String CLIENT_REGISTER_REDIS_PORT = "client.register.redis.port";

    public static final String CLIENT_REGISTER_ZK_URL = "client.register.zk.url";


    /**
     * ms
     */
    public static final String CLIENT_REGISTER_HEART_TIME = "client.register.heart.time";

    static final String CLIENT_CONNECT_TIMEOUT = "client.connect.timeout";


    public static final String CLIENT_REGISTER_KEY = "register-node";

}
