package com.chat.core.util;

import org.msgpack.MessagePack;


/**
 * MessagePackPool, 不建议使用 ,因为会创建大量的实例化对象, 效率极低
 *
 * @date:2019/11/8 16:39
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Deprecated
public class MessagePackPool {

    private static final ThreadLocal<MessagePack> packs = ThreadLocal.withInitial(MessagePack::new);

    /**
     * 获取一个 message pack
     *
     * @return
     */
    public static MessagePack getPack() {
        return packs.get();
    }


    /**
     * 移除它
     *
     * @return
     */
    public static void removePack() {
        if (null != packs.get()) {
            packs.remove();
        }
    }

}
