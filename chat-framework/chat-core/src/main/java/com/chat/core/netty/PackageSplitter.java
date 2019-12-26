package com.chat.core.netty;


import com.chat.core.model.NPack;
import com.chat.core.util.MessagePackPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 其实这个是没必要的 , 因为我们可以去继承一下
 * <p>
 * ChannelInboundHandlerAdapter - > LengthFieldBasedFrameDecoder -> PackageSplitter
 */
@Deprecated
public class PackageSplitter extends LengthFieldBasedFrameDecoder {

    /**
     * arg1 ; maxFrameLength：单个包最大的长度，这个值根据实际场景而定，我设置的是1024，固然我的心跳包不大，但是其他包可能比较大。
            * arg2 ; lengthFieldOffset : 表示数据长度字段开始的偏移量 ,我的前几个一个是版本号 一个是长度 , 此时是 4
            * arg3 ; lengthFieldLength : 数据长度字段的所占的字节数 , 我的是 2
            * arg4 ; lengthAdjustment :  修改帧数据长度字段中定义的值，可以为负数 因为有时候我们习惯把头部记入长度,若为负数,则说明要推后多少个字段
     * arg4 ; initialBytesToStrip 解析时候跳过多少个长度
     */
    public PackageSplitter() {
        super(Constants.MAX_FRAME_LENGTH, Constants.LENGTH_OFFSET, Constants.LENGTH_BYTES_COUNT, 0, 0, true);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 进来先获取版本号 -- > ridx +2 , 一致那么我们就继续
        if (in.readShort() == Constants.PROTOCOL_VERSION) {
            // 获取长度 -- > ridx +4 -- > 继续
            int len = in.readInt();

            // 创建一个零时数组 -- > 长度为 len
            byte[] read = new byte[len];

            // 开始读取
            // 第一个参数是  byte数组 ,
            // 第二个参数是  byte数组 的起始位置的索引
            // 第三个参数是  写入byte数组的长度 要小于等于 byte数组的长度
            in.readBytes(read, 0, len);

            // 获取messages
            NPack pack = MessagePackPool.getPack().read(read, NPack.class);

            //
            MessagePackPool.removePack();

            // 数组清空引用
            read = null;

            return pack;
        } else {
            // 否者我们不做处理
            return super.decode(ctx, in);
        }
    }

}
