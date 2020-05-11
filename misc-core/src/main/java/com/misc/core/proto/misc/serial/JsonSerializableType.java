package com.misc.core.proto.misc.serial;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.misc.core.exception.CodecException;
import com.misc.core.model.MiscPack;
import com.misc.core.proto.SerializableType;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Type;

/**
 * @date:2020/2/26 13:50
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class JsonSerializableType implements MiscSerializableHandler {

    /**
     * 序列号类型
     */
    private static final Type TYPE = new TypeReference<MiscPack>() {
    }.getType();

    public void encode(MiscPack msg, ByteBuf out) throws CodecException {
        byte[] body = JSON.toJSONBytes(msg);
        System.out.println(new String(body));
        out.writeInt(body.length);
        out.writeBytes(body);
    }

    public Object decode(ByteBuf in) throws CodecException {
        // 小于4直接返回
        if (in.readableBytes() < 4) {
            return SerializableType.NEED_MORE;
        }
        // 小于已读长度返回
        int len = in.readInt();
        if (in.readableBytes() < len) {
            return SerializableType.NEED_MORE;
        }
        byte[] body = new byte[len];
        in.readBytes(body, 0, len);
        return JSON.parseObject(body, TYPE);
    }
}
