package com.chat.spring.uitl;

import com.chat.spring.pojo.MessageDo;
import org.springframework.data.redis.core.BoundListOperations;

/**
 * TODO
 *
 * @date:2020/1/7 17:45
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MessageUtil {

    public static void saveMessage(BoundListOperations<String, Object> bls, MessageDo message) {
        bls.rightPush(message);
    }


    public static Object consumeMessage(BoundListOperations<String, Object> bls) {
        return bls.leftPop();
    }

}
