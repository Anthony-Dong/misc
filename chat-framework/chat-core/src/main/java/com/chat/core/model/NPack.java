package com.chat.core.model;


import com.chat.core.annotation.NotNull;
import com.chat.core.util.FileUtil;
import com.chat.core.util.JsonUtil;
import com.chat.core.util.RouterUtil;
import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;

import java.beans.Transient;
import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据包  所有 netty 传递的数据包
 * <p>
 * MessagePack , 核心数据包
 *
 * @date:2019/11/10 16:03
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Message
public class NPack implements Serializable {
    private static final long serialVersionUID = -4696439084835068210L;
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


    /**
     * 这个是方便 获取上下文ctx使用的.
     */
    private transient SocketAddress address;

    public SocketAddress getAddress() {
        return address;
    }

    public void setAddress(SocketAddress address) {
        this.address = address;
    }

    // 必须要有一个无参的构造器
    public NPack() {
    }


    public NPack(String router, byte[] body, long timestamp) {
        this.router = router;
        this.body = body;
        this.timestamp = timestamp;
    }

    public static final String ERROR = "";

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
                "router={" + decodeRouter() + '}' +
                ", timestamp=" + timestamp +
                ']';

    }

    public String decodeRouter() {
        if (this.router == null) {
            return ERROR;
        }

        try {
            return URLDecoder.decode(this.router, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return ERROR;
        }
    }


    public static NPack buildWithStringBody(@NotNull String sender, @NotNull String receiver, @NotNull String msg) {
        String router = RouterUtil.getRouterByString(RouterUtil.STRING_TYPE, sender, receiver);
        return new NPack(router, msg.getBytes());
    }

    public static NPack buildWithByteBody(@NotNull String sender, @NotNull String receiver, String fileName, @NotNull byte[] msg) {
        String router = RouterUtil.getRouterByFile(RouterUtil.BYTE_TYPE, sender, receiver, fileName);
        return new NPack(router, msg);
    }

    public static List<NPack> buildWithByteBody(@NotNull String sender, @NotNull String receiver, File file, long slice) {
        List<byte[]> list = null;
        try {
            list = FileUtil.cuttingFile(file, slice);

        } catch (Exception e) {
            //
        }
        if (list == null || list.size() == 0) {
            return Collections.emptyList();
        }
        List<NPack> nPackes = new ArrayList<>(list.size());

        String fileName = file.getName();
        list.forEach(e -> nPackes.add(buildWithByteBody(sender, receiver, fileName, e)));
        list = null;
        return nPackes;
    }


    public static <T> NPack buildWithJsonBody(@NotNull String sender, @NotNull String receiver, @NotNull T msg) {
        String classname = msg.getClass().getName();
        String router = RouterUtil.getRouterByJson(RouterUtil.JSON_TYPE, sender, receiver, classname);
        String json = JsonUtil.toJSONString(msg);
        return new NPack(router, json.getBytes());
    }


    /**
     * 清空引用的.
     */
    public void release() {
        this.body = null;
        this.address = null;
        this.router = null;
    }
}
