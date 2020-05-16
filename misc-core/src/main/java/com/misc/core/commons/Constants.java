package com.misc.core.commons;

import com.misc.core.proto.ProtocolType;
import com.misc.core.proto.SerializableType;
import com.misc.core.util.SystemUtil;

import static com.misc.core.commons.PropertiesConstant.*;

/**
 * 所有的常量信息 , 客户端服务器端同理, 懒得改配置了
 *
 * @date:2019/11/10 13:47
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class Constants {

    /**
     * 默认的上下午名称
     */
    public static final String DEFAULT_CONTEXT_NAME = "Misc-Context";

    /**
     * 服务版本号 , 默认是1,双方约定好的
     */
    public static final short DEFAULT_SERVER_VERSION = Short.parseShort(System.getProperty(CLIENT_SERVER_VERSION, "1"));


    /**
     * 协议类型，默认是 misc协议
     */
    public static final ProtocolType DEFAULT_PROTOCOL_TYPE = ProtocolType.MISC_PROTO;


    /**
     * 序列化类型，默认是 message pack
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
    public static final int DEFAULT_THREAD_SIZE = 10;

    /**
     * 无界队列,-1
     */
    public static final int DEFAULT_THREAD_QUEUE_SIZE = -1;

    /**
     * 超时时间,默认2000ms
     */
    public static final long DEFAULT_REQUEST_TIMEOUT = Long.getLong(CLIENT_REQUEST_TIMEOUT, 2000L);

    /**
     * 连接超时时间,默认2000ms
     */
    public static final long DEFAULT_CONNECT_TIMEOUT = Long.getLong(CLIENT_CONNECT_TIMEOUT, 2000L);

    /**
     * 客户端默认值心跳时间45S
     */
    public static final int DEFAULT_CLIENT_HEART_INTERVAL = Integer.getInteger(CLIENT_HEART_INTERVAL, 30);

    /**
     * 服务器默认心跳时间.90S
     */
    public static final int DEFAULT_SERVER_HEART_INTERVAL = Integer.getInteger(SERVER_HEART_INTERVAL, 60);


    /**
     * 系统的文件分隔符
     */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");


    /**
     * 默认的保存文件夹
     */
    private static final String FILE_TMP = "file";

    /**
     * 默认的保存路径
     */
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


    /**
     * 默认的清空文件管道的时间
     */
    public static final long DEFAULT_CLEAR_FILE_CHANNEL_TIME = 1000;


    /**
     * 默认的清空文件管道的时间
     */
    public static final int DEFAULT_OPEN_FILE_COUNT = 100;


    public static final int CURRENT_PROCESS_PID = SystemUtil.getCurrentProcessPid();
}
