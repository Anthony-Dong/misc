package com.chat.core.packutil;


import com.chat.core.model.NPack;
import com.chat.core.util.MessagePackPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 *  编码器 出站处理器
 *  ChannelOutboundHandlerAdapter  -- > MessageToByteEncoder -- > PackageEncoder
 */
public class PackageEncoder extends MessageToByteEncoder<NPack> {

    @Override
    protected void encode(ChannelHandlerContext ctx, NPack msg, ByteBuf out)
            throws Exception {

        // 将 message 转成 字节数组
        byte[] body = MessagePackPool.getPack().write(msg);

        // 获取 message 长度
        int length = body.length;

        // 协议头
        out.writeShort(Constants.PROTOCOL_VERSION);

        // 数据长度
        out.writeShort(length);

        // 数据体
        out.writeBytes(body);

        // 清空 message
        body = null;

        // 移除这个 MessagePack
        MessagePackPool.removePack();
    }
}
