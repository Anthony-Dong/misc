package com.chat.core.packutil;


import com.chat.core.model.NPack;
import com.chat.core.util.MessagePackPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 其实这个方法就行了 ..... 没必要用哪个 splitter 方法
 */
public class PackageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // r 指针
        int start = in.readerIndex();

        // 总长度 = 2+2+len
        int totalLength = in.readableBytes();

        // 进来先获取版本号 -- > ridx +2 , 一致那么我们就继续
        if (in.readShort() == Constants.PROTOCOL_VERSION) {

            // 获取长度 -- > ridx +2 -- > 继续
            short len = in.readShort();

            // 如果不等于
            if (len != (totalLength-4)) {
                // 复位 ....
                in.readerIndex(start);
                // 返回
                return;
            }

            // 创建一个零时数组 -- > 长度为 len
            byte[] read = new byte[len];

            // 开始读取
            // 第一个参数是  byte数组 ,
            // 第二个参数是  byte数组 的起始位置的索引
            // 第三个参数是  写入byte数组的长度 要小于等于 byte数组的长度
            in.readBytes(read, 0, len);

            // 获取messages
            NPack messages = MessagePackPool.getPack().read(read, NPack.class);

            // 添加到out中
            out.add(messages);

            // read数组清空引用
            read = null;
        } else {
            //复原不管 , 因为我们上面读取了俩字节 , 所以需要复原
            in.readerIndex(start);
            return;
        }
    }

}
