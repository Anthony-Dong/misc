package com.misc.core.proto.misc.serial;

import com.misc.core.exception.CodecException;
import com.misc.core.model.MiscPack;
import com.misc.core.proto.SerializableType;
import io.netty.buffer.ByteBuf;

/**
 * 直接使用基本类型进行转换的..
 *
 * @date:2020/3/17 18:39
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ByteSerializableType implements MiscSerializableHandler {


    public void encode(MiscPack msg, ByteBuf out) throws CodecException {
        // url
        byte[] url = msg.getRouter().getBytes();

        int url_len = url.length;

        // body
        int body_len = msg.getBody().length;

        out.writeInt(url_len);

        out.writeInt(body_len);

        out.writeBytes(url);

        out.writeBytes(msg.getBody());

        out.writeLong(msg.getTimestamp());
    }

    public Object decode(ByteBuf in) throws CodecException {
        if (in.readableBytes() < 8) return SerializableType.NEED_MORE;

        int url_len = in.readInt();

        int body_len = in.readInt();

        if (in.readableBytes() < url_len + body_len + 8) return SerializableType.NEED_MORE;

        byte[] url = new byte[url_len];

        byte[] body = new byte[body_len];

        in.readBytes(url, 0, url_len);

        in.readBytes(body, 0, body_len);

        long time = in.readLong();

        return new MiscPack(new String(url), body, time);
    }

}
