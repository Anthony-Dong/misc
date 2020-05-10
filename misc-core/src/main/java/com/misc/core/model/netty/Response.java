package com.misc.core.model.netty;

import com.misc.core.model.URL;
import com.misc.core.model.UrlConstants;

import java.io.Serializable;

/**
 * @date:2020/2/17 12:14
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Response implements Serializable {

    private static final long serialVersionUID = -5149563698001685410L;
    /**
     * 消息协议
     */
    private String protocol;

    /**
     * 消息ID 唯一表示
     */
    private String id;

    /**
     * 消息的URL
     */
    private URL url;

    /**
     * 消息体
     */
    private byte[] result;

    /**
     * 时间搓
     */
    private long timestamp;


    /**
     * 构造器
     */
    public Response(URL url, byte[] result, long timestamp) {
        this.url = url;
        this.protocol = url.getProtocol();
        this.id = url.getParameter(UrlConstants.ID_KEY);
        this.result = result;
        this.timestamp = timestamp;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getId() {
        return id;
    }

    public URL getUrl() {
        return url;
    }

    public byte[] getResult() {
        return result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Response{" +
                "protocol='" + protocol + '\'' +
                ", id='" + id + '\'' +
                ", url=" + url +
                ", result=" + convert() +
                ", timestamp=" + timestamp +
                '}';
    }

    private static final String NULL = "";

    private String convert() {
        if (this.result == null || result.length == 0) {
            return NULL;
        } else {
//            int len = body.length > 30 ? 30 : body.length;
            return new String(result);
        }
    }
}
