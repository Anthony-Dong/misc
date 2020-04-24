package com.chat.core.model.netty;

import com.chat.core.model.URL;
import com.chat.core.model.UrlConstants;

import java.io.Serializable;
import java.util.Arrays;

import static com.chat.core.model.UrlConstants.*;

/**
 * 请求对象 , 可以通过Npack 进行组装
 *
 * @date:2020/2/17 12:14
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Request implements Serializable {

    private static final long serialVersionUID = -6721350031693856128L;

    /**
     * 消息ID 唯一表示
     */
    private String id;

    /**
     * 消息协议
     */
    private String protocol;

    /**
     * 消息URL
     */
    private URL url;

    /**
     * 消息体
     */
    private byte[] body;

    /**
     * 超时时间 , -1代表永远不超时
     */
    private long timeout;

    /**
     * 时间搓 , 代表发送事件的时候
     */
    private long timestamp;

    /**
     * 这个传入是server端的host
     */
    private String host;

    /**
     * 这个也是server端的port
     */
    private int port;

    /**
     * 这个是版本号
     */
    private short version;

    /**
     * 构造方法 , 类似于HTTP
     *
     * @param url       URL
     * @param body      数据体
     * @param timestamp 时间搓
     */
    public Request(URL url, byte[] body, long timestamp) {
        this.url = url;
        this.protocol = url.getProtocol();
        this.id = url.getParameter(ID_KEY);
        this.body = body;
        this.timestamp = timestamp;
        this.timeout = url.getParameter(TIMEOUT_KEY, Long.MAX_VALUE);
    }

    public Request(URL url, byte[] body, long timestamp,String host,int port,short version) {
        this(url, body, timestamp);
        this.host = host;
        this.port = port;
        this.version = version;
    }


    public String getId() {
        return id;
    }

    public String getProtocol() {
        return protocol;
    }

    public URL getUrl() {
        return url;
    }

    public byte[] getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getTimeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", protocol='" + protocol + '\'' +
                ", url=" + url +
                ", body=" + convert() +
                ", timeout=" + timeout +
                ", timestamp=" + timestamp +
                '}';
    }

    public void release() {
        this.body = null;
        this.url = null;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public short getVersion() {
        return version;
    }


    public boolean needACK(){
        return this.url.getParameter(ACK_KEY, NO_ACK).equals(IS_ACK);
    }

    private static final String NULL = "";
    private String convert() {
        if (this.body == null||body.length==0) {
            return NULL;
        } else {
//            int len = body.length > 30 ? 30 : body.length;
            return new String(body);
        }
    }
}
