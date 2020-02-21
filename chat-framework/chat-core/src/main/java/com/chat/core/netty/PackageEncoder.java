package com.chat.core.netty;


import com.chat.core.model.NPack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

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
    private static final MessagePack pack=new MessagePack();

    public PackageEncoder(short version) {
        super();
        this.version = version;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NPack msg, ByteBuf out)
            throws Exception {
        int release = out.writerIndex();
        // 防止过大,直接去堆内存上,所以清空引用,促进回收
        byte[] body = null;
        try {
            // 1. 将 NPack 转换成 字节数组
            body = pack.write(msg);

            // 2. 获取 NPack 字节数组长度
            int length = body.length;

            // 3. 写入一个协议头 , 2个字节 16位 (-32768,32767)
            out.writeShort(version);

            // 4. 写入一个数据包长度 , 做校验  , 4个字节 32位 (-2147483648 , 2147483647 )
            out.writeInt(length);

            // 5. 写入数据体 - 真正的数据包
            out.writeBytes(body);

            // 6. 出现异常,直接重置
        } catch (Throwable error) {

            // 抓取任何异常 -> 出现异常 -> 重置
            out.writerIndex(release);
        } finally {
            // 清空 数组
            body = null;
            // 移除这个 MessagePack
        }
    }



}
