package com.chat.core.netty;

/**
 * 所有的常量信息 , 客户端服务器端同理
 *
 * @date:2019/11/10 13:47
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

import static com.chat.core.netty.PropertiesConstant.*;

public final class Constants {

    /**
     * 协议版本号 , 默认是1 ,最好是无符号整数 ,协议默认版本号  , 2个字节 16位 (-32768,32767)
     */
    public static final short PROTOCOL_VERSION = Short.parseShort(System.getProperty(CLIENT_VERSION, "1"));

    /**
     * 默认的地址
     */
    public static final String DEFAULT_HOST = System.getProperty(CLIENT_HOST, "0.0.0.0");
    public static final int DEFAULT_PORT = Integer.getInteger(CLIENT_PORT, 9999);

    /**
     * 默认处理器个数
     */
    public static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    /**
     * 线程名字
     */
    public static final String DEFAULT_THREAD_NAME = "Netty-Handler";
    /**
     * 默认大小是100个线程
     */
    public static final int DEFAULT_THREAD_SIZE = Integer.getInteger(CLIENT_THREAD_CORE_SIZE, 100);
    /**
     * 无界队列,-1
     */
    public static final int DEFAULT_QUEUE_SIZE = Integer.getInteger(CLIENT_THREAD_QUEUE_SIZE, -1);

    /**
     * 超时时间,默认2000ms
     */
    public static final long DEFAULT_TIMEOUT = Long.getLong(CLIENT_TIME_OUT, 2000);

    /**
     * 默认值30S
     */
    public static final int DEFAULT_HEART_INTERVAL = Integer.getInteger(CLIENT_HEART_INTERVAL, 30);


    /**
     * 启动最长等待时间
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = Integer.getInteger(CLIENT_CONNECT_TIMEOUT, 1000);

}
