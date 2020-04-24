package com.chat.core.netty;


import com.chat.core.model.NPack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器  将 {@link NPack} 编码成为  ByteBuf 然后放入字节缓冲区
 * <p>
 * 主要就是写 一个 版本号, 数据包长度, 数据包 , 来做校验
 * <p>
 * ChannelOutboundHandlerAdapter  -- > MessageToByteEncoder -- > PackageEncoder
 *
 * @date:2019/11/10 13:20
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class PackageEncoder extends MessageToByteEncoder<NPack> {

    /**
     * 自定义协议头
     */
    private final short version;
    /**
     * 写出类型
     */
    private final byte type;

    public PackageEncoder(short version, byte type) {
        super();
        this.version = version;
        this.type = type;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NPack msg, ByteBuf out)
            throws Exception {
        int save = out.writerIndex();
        // 魔数
        out.writeByte(CodecType.MAGIC_NUMBER);
        // 服务版本号
        out.writeShort(version);
        // 序列号类型
        out.writeByte(type);
        switch (type) {
            case CodecType.MESSAGE_PACK:
                MessagePackProtocol.encode(msg, out);
                break;
            case CodecType.GZIP_MESSAGE_PACK:
                MessagePackGzipProtocol.encode(msg, out);
                break;
            case CodecType.JSON_PACK:
                JsonProtocol.encode(msg, out);
                break;
            case CodecType.BYTE_PACK:
                ByteProtocol.encode(msg, out);
                break;
            default:
                out.writerIndex(save);
                handlerException(msg);
        }
    }

    /**
     * 无法处理类型
     */
    private void handlerException(NPack msg) {
        throw new RuntimeException(String.format("[客户端] 无法处理的类型 : %d , msg:%s.", type, msg));
    }

}
