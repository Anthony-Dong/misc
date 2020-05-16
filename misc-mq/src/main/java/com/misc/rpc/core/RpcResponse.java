package com.misc.rpc.core;

import com.misc.core.serialization.Deserializer;

/**
 * 响应结果
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcResponse {

    /**
     * 当前服务器的host
     */
    private String host;

    /**
     * 当前服务器的port
     */
    private int port;

    /**
     * 服务版本号
     */
    private short serverVersion;

    /**
     * 时间搓
     */
    private long timeStamp;

    /**
     * 当前服务进程id
     */
    private int pid;

    /**
     * 响应需要接收方反序列化
     */
    private transient Deserializer<Object> serializable;

    /**
     * 响应结果
     */
    private Object result;




    public void setServerVersion(short serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setSerializable(Deserializer<Object> serializable) {
        this.serializable = serializable;
    }

    public void setResult(Object result) {
        this.result = result;
    }



    public short getServerVersion() {
        return serverVersion;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getPid() {
        return pid;
    }

    public Deserializer<Object> getSerializable() {
        return serializable;
    }

    public Object getResult() {
        return result;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
