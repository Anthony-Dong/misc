package com.chat.core.netty;

import com.alibaba.fastjson.JSON;
import com.chat.core.model.NPack;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

import static java.lang.Float.TYPE;

/**
 * 直接使用基本类型进行转换的..
 *
 * @date:2020/3/17 18:39
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ByteProtocol {

    public static void encode(NPack msg, ByteBuf out) throws IOException {
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

    public static Object decode(ByteBuf in) throws IOException {
        if (in.readableBytes() < 8) return CodecType.NEED_MORE;

        int url_len = in.readInt();

        int body_len = in.readInt();

        if (in.readableBytes() < url_len + body_len + 8) return CodecType.NEED_MORE;

        byte[] url = new byte[url_len];

        byte[] body = new byte[body_len];

        in.readBytes(url, 0, url_len);

        in.readBytes(body, 0, body_len);

        long time = in.readLong();

        return new NPack(new String(url), body, time);
    }

}
