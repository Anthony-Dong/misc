package com.chat.core.netty;

import static com.chat.core.netty.PropertiesConstant.*;

/**
 * 所有的常量信息 , 客户端服务器端同理, 懒得改配置了
 *
 * @date:2019/11/10 13:47
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class Constants {

    /**
     * 协议版本号 , 默认是1,双方约定好的
     */
    public static final short PROTOCOL_VERSION = Short.parseShort(System.getProperty(CLIENT_PROTOCOL_VERSION, "1"));

    /**
     * 服务类型  ->  client-content-type
     * 1- MessagePack
     * 2- ZIP+MessagePack
     */
    public static final SerializableType DEFAULT_SERIALIZABLE_TYPE = SerializableType.MESSAGE_PACK;

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
    public static final int DEFAULT_THREAD_SIZE = 100;
    /**
     * 无界队列,-1
     */
    public static final int DEFAULT_QUEUE_SIZE = -1;

    /**
     * 超时时间,默认2000ms
     */
    public static final long DEFAULT_TIMEOUT = Long.getLong(CLIENT_TIME_OUT, 1000L);

    /**
     * 客户端默认值心跳时间45S
     */
    public static final int DEFAULT_CLIENT_HEART_INTERVAL = Integer.getInteger(CLIENT_HEART_INTERVAL, 45);

    /**
     * 服务器默认心跳时间.90S
     */
    public static final int DEFAULT_SERVER_HEART_INTERVAL = Integer.getInteger(SERVER_HEART_INTERVAL, 90);
    /**
     * 启动最长等待时间
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 1000;


    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final String FILE_TMP = "file";

    public static final String DEFAULT_FILE_DIR = getFileDir();

    /**
     * 保存文件的位置,设置.
     */
    private static String getFileDir() {
        String file = System.getProperty(CLIENT_FILE_DIR);
        if (file == null || file.isEmpty()) {
            file = System.getProperty("user.dir") + FILE_SEPARATOR + FILE_TMP;
        }
        return file;
    }

}
