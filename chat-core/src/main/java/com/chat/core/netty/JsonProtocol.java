package com.chat.core.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chat.core.model.NPack;
import com.chat.core.util.FileUtil;
import io.netty.buffer.ByteBuf;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @date:2020/2/26 13:50
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class JsonProtocol {

    private static final Type TYPE = new TypeReference<NPack>() {
    }.getType();

    public static void encode(NPack msg, ByteBuf out) throws IOException {
        byte[] body = JSON.toJSONBytes(msg);
        out.writeInt(body.length);
        out.writeBytes(body);
    }

    public static Object decode(ByteBuf in) throws IOException {
        // 小于4直接返回
        if (in.readableBytes() < 4) {
            return CodecType.NEED_MORE;
        }
        // 小于已读长度返回
        int len = in.readInt();
        if (in.readableBytes() < len) {
            return CodecType.NEED_MORE;
        }
        byte[] body = new byte[len];
        in.readBytes(body, 0, len);
        return JSON.parseObject(body, TYPE);
    }
}
