package com.http.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.net.SocketAddress;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * HttpProcessHandler 处理器
 *
 * @date:2019/12/18 20:08
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class HttpProcessHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * 或者实现 @ChannelHandler.Sharable
     *
     * @return
     */
    @Override
    public boolean isSharable() {
        return true;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        SocketAddress address = channelHandlerContext.channel().remoteAddress();
        // 请求方法
        fullHttpRequest.method();

        // 请求URI
        fullHttpRequest.uri();


        // 请求头
        fullHttpRequest.headers();

        // 请求体
        ByteBuf content = fullHttpRequest.content();

        String json = "[\"a\",\"b\",\"c\"]";


        // 响应
        DefaultHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(json, CharsetUtil.UTF_8));
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, json.getBytes().length);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/json");

        // 不需要解码器直接写出去
        channelHandlerContext.writeAndFlush(response);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            ctx.close();
        } else {
            cause.printStackTrace();
        }
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

}
