package com.chat.core.netty;

import com.chat.core.model.NPack;
import com.chat.core.util.FileUtil;
import io.netty.buffer.ByteBuf;
import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * @date:2020/2/24 18:06
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MessagePackGzipProtocol {
    /**
     * 编解码器
     */
    private static final MessagePack pack = new MessagePack();

    public static void encode(NPack msg, ByteBuf out) throws IOException {
        byte[] body = pack.write(msg);
        byte[] gzip = FileUtil.gzip(body);
        out.writeInt(gzip.length);
        out.writeBytes(gzip);
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
        byte[] bytes = FileUtil.unGzip(body);
        return pack.read(bytes, NPack.class);
    }
}
