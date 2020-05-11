package com.misc.core.proto.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.misc.core.commons.Constants;
import com.misc.core.commons.PropertiesConstant;
import com.misc.core.env.MiscProperties;
import com.misc.core.model.MiscPack;
import com.misc.core.proto.misc.serial.MiscSerializableHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;


/**
 * HTTP处理器
 *
 * @date: 2020-05-10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class HttpCodec extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpCodec.class);
    private final Byte type;
    private final Map<Byte, MiscSerializableHandler> serializeHandlerMap;
    private static final Type TYPE = new TypeReference<MiscPack>() {
    }.getType();

    public HttpCodec(MiscProperties properties, Map<Byte, MiscSerializableHandler> serializeHandlerMap) {
        this.type = properties.getByte(PropertiesConstant.CLIENT_SERIALIZABLE_TYPE, Constants.DEFAULT_SERIALIZABLE_TYPE.getCode());
        this.serializeHandlerMap = serializeHandlerMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            try {
                MiscPack miscPack = handlerRequest(request);
                ctx.fireChannelRead(miscPack);
            } catch (Exception e) {
                logger.error("[Misc-Server] Happened exception client-ip: {}, exception: {}.", ctx.channel().remoteAddress(), e.getMessage());
                ByteBuf buffer = ctx.alloc().buffer();
                try {
                    ctx.writeAndFlush(writeError(buffer, e.getMessage()));
                } finally {
                    ctx.close();
                }
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    private static MiscPack handlerRequest(FullHttpRequest request) {
        if (HttpMethod.POST != request.method()) {
            throw new RuntimeException("不支持的请求协议,请使用POST请求协议");
        }
        String uri = request.getUri();
        System.out.println(uri);
        ByteBuf content = request.content();
        byte[] body = new byte[content.readableBytes()];
        content.readBytes(body);
        System.out.println(new String(body));
        return JSON.parseObject(body, TYPE);
    }


    /**
     * 写错误
     */
    private static FullHttpResponse writeError(ByteBuf buf, String msg) {
        buf.writeBytes(JSON.toJSONBytes(Collections.singletonMap("error", msg)));
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, buf);
    }


    /**
     * 做一次转换
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        byte[] bytes = JSON.toJSONBytes(msg);
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(bytes);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);

        // todo 添加拦截器
        super.write(ctx, response, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        try {
            super.flush(ctx);
        } finally {
            ctx.close();
        }
    }
}
