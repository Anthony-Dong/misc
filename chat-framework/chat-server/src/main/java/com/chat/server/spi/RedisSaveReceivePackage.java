package com.chat.server.spi;

import com.chat.core.exception.HandlerException;
import com.chat.core.model.Message;
import com.chat.core.model.NPack;
import com.chat.core.exception.ExceptionHandler;
import com.chat.core.util.JsonUtil;
import com.chat.server.util.RedisPool;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Properties;
import java.util.Random;

import static com.chat.core.util.RouterUtil.*;
import static com.chat.core.util.Assert.*;
import static com.chat.server.util.RedisPool.loadRedisPool;

/**
 * 用redis 来做存储
 *
 * @date:2019/12/26 20:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class RedisSaveReceivePackage implements SaveReceivePackage {

    private final RedisPool redisPool;


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
            throw ExceptionHandler.createHandlerException(RedisSaveReceivePackage.class, "NPack router 不符合URL编码格式要求 ! ");
        }
        String type = properties.getProperty(TYPE);
        if (type == null) {
            throw ExceptionHandler.createHandlerException(RedisSaveReceivePackage.class, "NPack router 不符合URL编码格式要求 ! ");
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
    private void doByteArray(NPack pack, Properties properties) throws HandlerException {

        String filename = properties.getProperty(FILE_NAME);
        assertNotNull(filename);

        String fileName = FILE_SYSTEM + filename;

        try {
            RandomAccessFile file1 = new RandomAccessFile(fileName, "rw");
            file1.write(pack.getBody(), 0, new Random().nextInt(100));
            file1.close();
            // 模拟异常
            int x = 1 / 0;
        } catch (Exception e) {
            throw ExceptionHandler.createHandlerException(RedisSaveReceivePackage.class, "文件上传失败");
        }

        System.out.println("doByteArray : " + pack);
    }

    /**
     * 处理JSON
     */
    private void doJSON(NPack pack, Properties properties) throws HandlerException {
        System.out.println("doJSON : " + pack);
    }


    /**
     * 处理字符串
     */
    private void doString(NPack pack, Properties properties) throws HandlerException {
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


}
