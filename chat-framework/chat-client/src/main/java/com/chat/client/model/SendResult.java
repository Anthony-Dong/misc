package com.chat.client.model;



/**
 * @date:2019/11/14 14:55
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class SendResult {

    private int code;

    private String msg;

    private long uuid;

    public SendResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public SendResult(int code, String msg, long uuid) {
        this.code = code;
        this.msg = msg;
        this.uuid = uuid;
    }



    public static SendResult success(long uuid) {

        return new SendResult(1, "OK",uuid);
    }


    public static final SendResult error = new SendResult(0, "ERROR");




    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "SendResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}
