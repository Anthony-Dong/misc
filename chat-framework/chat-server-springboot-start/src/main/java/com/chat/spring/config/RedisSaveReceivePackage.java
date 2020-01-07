package com.chat.spring.config;

import com.chat.core.exception.ExceptionHandler;
import com.chat.core.exception.HandlerException;
import com.chat.core.model.Message;
import com.chat.core.model.NPack;
import com.chat.core.util.FileUtil;
import com.chat.server.spi.SaveReceivePackage;
import com.chat.spring.pojo.MessageDo;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.util.Properties;

import static com.chat.core.util.Assert.assertNotNull;
import static com.chat.core.util.RouterUtil.*;

/**
 * 用redis 来做存储
 *
 * @date:2019/12/26 20:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class RedisSaveReceivePackage implements SaveReceivePackage {

    private RedisTemplate<String, Object> redisTemplate;

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
        this.FILE_SYSTEM = mkdir();
        redisTemplate = ChatConfigBeanPostProcess.redisTemplate;
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
        File file = new File(fileName);

        byte[] body = pack.getBody();
        try {
            FileUtil.mergingFile(file, body);
        } catch (Exception e) {
            throw ExceptionHandler.createHandlerException(RedisSaveReceivePackage.class, e.getMessage());
        } finally {
            body = null;
        }

        System.out.println("doByteArray : " + pack);
    }


    /**
     * 处理JSON
     */
    private void doJSON(NPack pack, Properties properties) throws HandlerException {
        // todo
    }


    /**
     * 处理字符串
     */
    private void doString(NPack pack, Properties properties) throws HandlerException {

        String receiver = properties.getProperty(RECEIVER);

        assertNotNull(receiver);

        String sender = properties.getProperty(SENDER);

        assertNotNull(sender);

        String msg = new String(pack.getBody());

        Message message = new Message(receiver, msg, pack.getTimestamp());

        BoundListOperations<String, Object> rj = redisTemplate.boundListOps(receiver);

        rj.rightPush(message);

        System.out.println(receiver + " : " + message);
    }


    static void saveMessage(BoundListOperations<String, MessageDo> bls, MessageDo message) {
        bls.rightPush(message);
    }


    static MessageDo consumeMessage(BoundListOperations<String, MessageDo> bls) {
        return bls.leftPop();
    }
}
