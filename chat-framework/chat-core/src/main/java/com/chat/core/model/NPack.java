package com.chat.core.model;


import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;
/**
 *
 *  数据包  所有 netty 传递的数据包
 *
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
     * 由于 这玩意不能有强类型 , 所以需要Json
     */
    @Index(1)
    private String json;

    // 时间错  , 默认不用设置
    @Index(2)
    private long timestamp = System.currentTimeMillis();


    // 必须要有一个无参的构造器
    public NPack() {
    }


    public NPack(String router, String json, long timestamp) {
        this.router = router;
        this.json = json;
        this.timestamp = timestamp;
    }


    public NPack(String router, String json) {
        this.router = router;
        this.json = json;
    }

    public NPack(String router) {
        this.router = router;
    }

    public String getRouter() {
        return router;
    }


    public void setRouter(String router) {
        this.router = router;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return "NPack{" +
                "router='" + router + '\'' +
                ", json='" + json + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }


}
