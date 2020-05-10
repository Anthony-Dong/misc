package com.misc.core.model;

import com.misc.core.model.netty.Request;

/**
 * 用来生成请求的
 *
 * @date: 2020-05-10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RequestBuilder {

    private Request request;

    public RequestBuilder() {
        this.request = new Request(null,null,1L);
    }

    public Request build() {
        return request;
    }


}
