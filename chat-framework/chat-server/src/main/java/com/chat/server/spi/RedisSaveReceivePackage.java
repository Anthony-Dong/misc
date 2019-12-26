package com.chat.server.spi;

import com.chat.core.exception.HandlerException;
import com.chat.core.model.Message;
import com.chat.core.model.NPack;
import com.chat.core.util.JsonUtil;
import com.chat.server.util.RedisPool;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;

import static com.chat.core.util.RouterUtil.*;
import static com.chat.core.util.Assert.*;

/**
 * 用redis 来做存储
 *
 * @date:2019/12/26 20:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class RedisSaveReceivePackage implements SaveReceivePackage {

    private final RedisPool redisPool;
    private static final String REDIS_HOST = "redis.host";
    private static final String REDIS_PORT = "redis.port";
    private static final String REDIS_POOL = "redis.pool.size";
    private static final String DEFAULT_HOST = "localhost";
    private static final String PROPERTIES_PATH_NAME = "chat-server.properties";

    // 1. 文件目录 , 默认在 ${user.dir}/upload  下面
    private final String FILE_SYSTEM;

    private String mkdir() {
        String property = System.getProperty("user.dir");
        String dir = property + "\\upload";
        File file = new File(dir);
        file.mkdir();
        return dir + "\\";
    }

    /**
     * 初始化加载
     */
    public RedisSaveReceivePackage() {
        this.redisPool = loadRedisPool();
        this.FILE_SYSTEM = mkdir();
    }

    /**
     * 主 处理逻辑
     *
     * @param pack NPack 数据包
     * @throws HandlerException 异常
     */
    @Override
    public void doSave(NPack pack) throws HandlerException {
        String router = pack.getRouter();
        Properties properties = convertRouter(router);
        if (null == properties) {
            throw new HandlerException("NPack router 不符合要求 ! ");
        }
        String type = properties.getProperty(TYPE);
        if (type == null) {
            throw new HandlerException("NPack type 不符合要求 ! ");
        }
        switch (type) {
            case STRING_TYPE:
                doString(pack, properties);
                break;
            case BYTE_TYPE:
                doByteArray(pack, properties);
                break;
            case JSON_TYPE:
                doJSON(pack, properties);
                break;
            default:
                break;
        }
    }


    /**
     * 处理文件
     */
    private void doByteArray(NPack pack, Properties properties) {

        String filename = properties.getProperty(FILE_NAME);
        assertNotNull(filename);

        String fileName = FILE_SYSTEM + filename;

        try {
            RandomAccessFile file1 = new RandomAccessFile(fileName, "rw");
            file1.write(pack.getBody());
            file1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("doByteArray : " + pack);
    }

    /**
     * 处理JSON
     */
    private void doJSON(NPack pack, Properties properties) {
        System.out.println("doJSON : " + pack);
    }


    /**
     * 处理字符串
     */
    private void doString(NPack pack, Properties properties) {
        String receiver = properties.getProperty(RECEIVER);
        assertNotNull(receiver);
        String sender = properties.getProperty(SENDER);
        assertNotNull(sender);


        Jedis jedis = redisPool.get();


        String msg = new String(pack.getBody());
        Message message = new Message(receiver, msg, pack.getTimestamp());


        String json = JsonUtil.toJSONString(message);

        // REDIS 存入
        jedis.lpush(receiver, json);


        redisPool.remove(jedis);

        System.out.println("doString : " + pack);
        System.out.println("doString message : " + message);
    }


    /**
     * 连接Redis用的
     * 默认使用的是 系统属性,
     * 其次使用的是:  classpath: chat-server.properties
     *
     * @return RedisPool
     */
    private static RedisPool loadRedisPool() {
        // 属性
        String s_host = System.getProperty(REDIS_HOST, DEFAULT_HOST);
        String s_port = System.getProperty(REDIS_PORT);
        String s_size = System.getProperty(REDIS_POOL);
        if (null == s_port) {
            Properties properties = new Properties();
            try {
                properties.load(SaveReceivePackage.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String p_host = properties.getProperty(REDIS_HOST, DEFAULT_HOST);
            String p_port = properties.getProperty(REDIS_PORT);
            String p_size = properties.getProperty(REDIS_POOL);
            return new RedisPool(null == p_size ? 10 : Integer.parseInt(p_size.trim()), new HostAndPort(p_host, Integer.parseInt(p_port.trim())));
        } else {
            return new RedisPool(null == s_size ? 10 : Integer.parseInt(s_size.trim()), new HostAndPort(s_host, Integer.parseInt(s_port.trim())));
        }
    }
}
