package com.chat.core.model;


import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCounted;
import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.net.URLDecoder;
import java.util.Arrays;

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
     * 路由信息, 其实就是URL .请看 {@link URL} 和 {@link RouterBuilder}
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

    public NPack(String router, long timestamp) {
        this(router, null, timestamp);
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


//    @Override
//    public String toString() {
//        return "NPack[" +
//                "router={" + decodeRouter() + '}' +
//                ", timestamp=" + timestamp +
//                ']';
//
//    }


    @Override
    public String toString() {
        return "NPack{" +
                "router='" + decodeRouter() + '\'' +
                ", body=" + convert() +
                ", timestamp=" + timestamp +
                '}';
    }

    private String convert() {
        if (this.body == null || body.length == 0) {
            return ERROR;
        } else {
//            int len = body.length > 30 ? 30 : body.length;
            return new String(body);
        }
    }


    private String decodeRouter() {
        if (this.router == null) {
            return ERROR;
        }
        try {
            return URLDecoder.decode(this.router, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return ERROR;
        }
    }


    /**
     * {@link SimpleChannelInboundHandler#channelRead} 第112行
     * 这里会帮助我们release掉对象. 释放内存.
     *
     * @return true . 无所谓的true/false
     */
    public boolean release() {
        this.body = null;
        this.address = null;
        this.router = null;
        return true;
    }

}
