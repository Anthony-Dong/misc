package com.misc.core.proto.misc.serial;

import com.misc.core.exception.CodecException;
import com.misc.core.model.MiscPack;
import com.misc.core.proto.SerializableType;
import com.misc.core.util.FileUtil;
import io.netty.buffer.ByteBuf;
import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * @date:2020/2/24 18:06
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class GzipMessagePackSerializableType implements MiscSerializableHandler {
    /**
     * 编解码器
     */
    private static final MessagePack pack = new MessagePack();

    public void encode(MiscPack msg, ByteBuf out) throws CodecException {
        try {
            byte[] body = pack.write(msg);
            byte[] gzip = FileUtil.gzip(body);
            out.writeInt(gzip.length);
            out.writeBytes(gzip);
        } catch (IOException e) {
            throw new CodecException(e);
        }

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
        try {
            byte[] bytes = FileUtil.unGzip(body);
            return pack.read(bytes, MiscPack.class);
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }
}
