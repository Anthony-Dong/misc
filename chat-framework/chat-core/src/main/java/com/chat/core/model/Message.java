package com.chat.core.model;

/**
 * 存入 redis 的消息体
 * @date:2019/12/26 21:30
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class Message {

    private String receiver;

    private long timestamp;

    private String msg;

    public Message(String receiver, String msg, long timestamp) {
        this.receiver = receiver;
        this.msg = msg;
        this.timestamp = timestamp;
    }

    public Message() {
    }

    @Override
    public String toString() {
        return "Message{" +
                "receiver='" + receiver + '\'' +
                ", timestamp=" + timestamp +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
