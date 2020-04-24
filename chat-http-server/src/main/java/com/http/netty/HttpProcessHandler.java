package com.http.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

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
     */
    @Override
    public boolean isSharable() {
        return true;
    }


    private FileHandler handler = new FileHandler();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        handler.addMapping("/a", "/b");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        HttpResponse resp = this.handler.handler(channelHandlerContext, fullHttpRequest);
        // 不需要解码器直接写出去
        channelHandlerContext.writeAndFlush(resp);
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
