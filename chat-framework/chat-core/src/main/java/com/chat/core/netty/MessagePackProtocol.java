package com.chat.core.netty;

import com.chat.core.model.NPack;
import io.netty.buffer.ByteBuf;
import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * @date:2020/2/24 17:44
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MessagePackProtocol {
    /**
     * 编解码器
     */
    private static final MessagePack pack = new MessagePack();

    /**
     * 编码器 : 长度+字节数组
     */
    public static void encode(NPack msg, ByteBuf out) throws IOException {
        byte[] write = pack.write(msg);
        int length = write.length;
        out.writeInt(length);
        out.writeBytes(write);
    }

    /**
     * 解码器 : 长度 字节数组
     */
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
        return pack.read(body, NPack.class);
    }

}
