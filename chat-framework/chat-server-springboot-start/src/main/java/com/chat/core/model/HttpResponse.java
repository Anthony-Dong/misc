package com.chat.core.model;

/**
 *  响应体
 *
 *  发送消息就不需要使用泛型
 *
 * @date:2020/1/7 17:57
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class HttpResponse {

    public HttpResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    private static final String ok = "ok";

    private static final String fail = "fail";

    private String status;

    private Object body;


    private HttpResponse(String status, Object body) {
        this.status = status;
        this.body = body;
    }

    public static HttpResponse success(Object body) {
        return new HttpResponse(ok, body);
    }

    public static HttpResponse fail(Object body) {
        return new HttpResponse(fail, body);
    }
}
