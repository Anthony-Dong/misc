package com.chat.core.netty;

/**
 * 所有的常量信息 , 客户端服务器端同理
 *
 * @date:2019/11/10 13:47
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

import java.io.File;
import java.util.Properties;

import static com.chat.core.netty.PropertiesConstant.*;

public final class Constants {

    /**
     * Npack协议版本号 , 默认是1
     */
    public static final short PROTOCOL_VERSION = Short.parseShort(System.getProperty(CLIENT_VERSION, "10010"));

    /**
     * 文件协议
     */
    public static final short FILE_PROTOCOL_VERSION = Short.parseShort(System.getProperty(CLIENT_FILE_PROTOCOL, "20001"));

    /**
     * 读取文件标识符
     */
    public static final short FILE_START_VERSION = Short.parseShort(System.getProperty(CLIENT_FILE_START, "30001"));

    /**
     * 关闭文件标识符
     */
    public static final short FILE_END_VERSION = Short.parseShort(System.getProperty(CLIENT_FILE_END, "30002"));

    public static final short FILE_NEED_RESPONSE = 30003;

    public static final short FILE_NULL_RESPONSE = 30004;

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


    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    public static final String FILE_TMP = "file";

    public static final String DEFAULT_FILE_DIR = System.getProperty("user.dir") + FILE_SEPARATOR + FILE_TMP;


    static {
        File file = new File(DEFAULT_FILE_DIR);
        if (!file.exists()) {
            boolean mkdir = file.mkdir();
        }
    }
}
