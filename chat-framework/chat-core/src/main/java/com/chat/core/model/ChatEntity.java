package com.chat.core.model;

/**
 *
 *
 * @date:2019/11/13 16:30
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ChatEntity {

    /**
     * 1  web
     * 2  手机 (安卓)
     * 3  手机(IOS)
     * 4  平板(ios)
     * 5 平板(ios)
     */
    private Integer facility;

    /**
     * 其实就是谁发送的
     */
    private String sender;


    /**
     * 谁接收
     */
    private String receiver;


    /**
     * 发送者 IP ,这里不考虑端口号的问题
     */
    private String ip;


    /**
     * 发送者 客户端
     */
    private String msg;


    private long timestamp;


    public ChatEntity() {
        this.timestamp = System.currentTimeMillis();
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getFacility() {
        return facility;
    }

    public void setFacility(Integer facility) {
        this.facility = facility;
    }

    @Override
    public String toString() {
        return "ChatEntity{" +
                "facility=" + facility +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", ip='" + ip + '\'' +
                ", msg='" + msg + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
