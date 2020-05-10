package com.misc.client.context;

import com.misc.core.exception.TimeOutException;
import com.misc.core.model.netty.Response;

import java.util.function.Consumer;

/**
 * msg 协议
 *
 * @date:2020/2/18 20:17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface MsgContext {

    /**
     * 需要得到相应
     * @throws TimeOutException
     */
    Response sendMessageBySync(String msg, String sender, String receiver) throws TimeOutException;

    /**
     * 直接发送无序响应
     */
    void sendMessage(String msg, String sender, String receiver);

    /**
     * 异常
     */
    void senderMessageWithThrowable(String msg, String sender, String receiver, Consumer<TimeOutException> e);

}
