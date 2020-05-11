package com.misc.core.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.misc.core.model.netty.Arg;
import com.misc.core.model.netty.Request;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.List;

/**
 * 用来生成请求的
 *
 * @date: 2020-05-10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RequestBuilder {

    private Request request;

    public RequestBuilder() {
        this.request = new Request(null, null, 1L);
    }

    public Request build() {
        return request;
    }


    public static void main(String[] args) {
        final Type TYPE = new TypeReference<List<Arg>>() {
        }.getType();
        Object o = JSON.parseObject("W3siY2xhenoiOiJqYXZhLmxhbmcuU3RyaW5nIiwiaW5kZXgiOjAsInZhbHVlIjoiXCJoZWxsbyB3b3JsZFwiIn1d".getBytes(), TYPE);

        System.out.println(o);

        String str = URLDecoder.decode("W3siY2xhenoiOiJqYXZhLmxhbmcuU3RyaW5nIiwiaW5kZXgiOjAsInZhbHVlIjoiXCJoZWxsbyB3b3JsZFwiIn1d");

        System.out.println(str);
    }

}
