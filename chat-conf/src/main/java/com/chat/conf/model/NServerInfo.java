package com.chat.conf.model;


/**
 * 每一个  chat server 的节点信息
 *
 * @date:2019/11/12 21:34
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class NServerInfo {


    private String host;


    private int port;

    private Integer totalConnection;

    private long timestamp;


    public NServerInfo() {
        this.timestamp = System.currentTimeMillis();
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

    public Integer getTotalConnection() {
        return totalConnection;
    }

    public void setTotalConnection(Integer totalConnection) {
        this.totalConnection = totalConnection;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public boolean equals(Object obj) {
        NServerInfo info=null;
        if (obj instanceof NServerInfo) {
            info = (NServerInfo) obj;
        }
        return info.getHost().equals(this.getHost()) && info.getPort() ==this.getPort();
    }

    /**
     * hashSet 去重使用的 , 第一回来比较 hashCode()值 ,相同比较 equals() 方法 ....
     */
    @Override
    public int hashCode() {
        int port = this.getPort();
        String host = this.getHost();
        return port+host.hashCode();
    }

    @Override
    public String toString() {
        return "NServerInfo{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", totalConnection=" + totalConnection +
                ", timestamp=" + timestamp +
                '}';
    }

}
