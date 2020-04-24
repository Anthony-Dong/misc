package com.http.netty;

import com.chat.core.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * TODO
 *
 * @date:2020/3/17 19:07
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class FileHandler {

    // path= get/static/{value}
    private final Map<String, String> map = new HashMap<>();

    public void addMapping(String path, String localPath) {
        String[] split = StringUtils.split(path, '/');




        map.put(path, localPath);
    }

    private static final DefaultFullHttpResponse NOT_FOUND = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND);


    public HttpResponse handler(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
        String uri = fullHttpRequest.uri();
        String path = map.get(uri);
        if (path == null) return NOT_FOUND;
        String json = "[\"a\",\"b\",\"c\"]";

        // 响应
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, channelHandlerContext.alloc().directBuffer().writeBytes(json.getBytes()));
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, json.getBytes().length);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/json");
        return response;

//        FileInputStream stream = new FileInputStream();
    }

}
