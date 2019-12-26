package com.chat.core.model;


import com.chat.core.annotation.NotNull;
import com.chat.core.util.JsonUtil;
import com.chat.core.util.RouterUtil;
import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;

/**
 * 数据包  所有 netty 传递的数据包
 * <p>
 * MessagePack , 核心数据包
 *
 * @date:2019/11/10 16:03
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Message
public class NPack {
    /**
     * 名字
     */
    @Index(0)
    private String router;

    /**
     * 由于灵活性的问题我们采用byte
     */
    @Index(1)
    private byte[] body;

    // 时间错  , 默认不用设置
    @Index(2)
    private long timestamp;


    // 必须要有一个无参的构造器
    public NPack() {
    }


    public NPack(String router, byte[] body, long timestamp) {
        this.router = router;
        this.body = body;
        this.timestamp = timestamp;
    }


    public NPack(String router, byte[] body) {
        this(router, body, System.currentTimeMillis());
    }

    public NPack(String router) {
        this(router, null);
    }

    public String getRouter() {
        return router;
    }


    public void setRouter(String router) {
        this.router = router;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return "NPack[" +
                "router={" + router + '}' +
                ", timestamp=" + timestamp +
                ']';
    }


    public static NPack buildWithStringBody(@NotNull String sender, @NotNull String receiver, @NotNull String msg) {
        String router = RouterUtil.getRouterByString(RouterUtil.STRING_TYPE, sender, receiver);
        return new NPack(router, msg.getBytes());
    }

    public static NPack buildWithByteBody(@NotNull String sender, @NotNull String receiver, String FileName, @NotNull byte[] msg) {
        String router = RouterUtil.getRouterByFile(RouterUtil.BYTE_TYPE, sender, receiver, FileName);
        return new NPack(router, msg);
    }


    public static <T> NPack buildWithJsonBody(@NotNull String sender, @NotNull String receiver, @NotNull T msg) {
        String classname = msg.getClass().getName();
        String router = RouterUtil.getRouterByJson(RouterUtil.JSON_TYPE, sender, receiver, classname);
        String json = JsonUtil.toJSONString(msg);
        return new NPack(router, json.getBytes());
    }
}
