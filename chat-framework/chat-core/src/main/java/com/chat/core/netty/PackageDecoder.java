package com.chat.core.netty;


import com.chat.core.model.NPack;
import com.chat.core.model.NpackBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.ArrayList;
import java.util.List;

/**
 * 解码器会很麻烦
 * <p>
 * 主要分为 4种情况
 * <p>
 * 1. 缓冲区只有一个数据包,此时只用做 版本校验 , 长度校验 , 然后读就可以了
 * 2. 缓冲区有多个数据包 , 可能是整数的倍数 , 就需要迭代读取
 * 3. 缓冲区可能有多个数据包 , 可能出现半个包的问题, 比如 2.5个 包, 此时就需要解码时注意
 * 4. 如果出现半个+整数个, 前面根本无法解码 , 此时就无法处理 , 可能出现丢包
 * <p>
 * 所以我们要求的是数据传输的完整性,最低要求将数据包完整的传输和接收
 *
 * @date:2019/11/10 13:40
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public final class PackageDecoder extends ByteToMessageDecoder {

    /**
     * 默认值是 {@link com.chat.core.netty.Constants#PROTOCOL_VERSION}
     */
    private final short VERSION;
    private static final MessagePack pack = new MessagePack();

    /**
     * 构造方法
     */
    public PackageDecoder(short version) {
        super();
        this.VERSION = version;
    }

    /**
     * {@link ByteToMessageDecoder#channelRead}
     * <p>
     * 解码器
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        handler(in, out);
    }

    /**
     * 处理器 - 主要处理逻辑
     */
    private void handler(ByteBuf in, List<Object> out) {
        // 如果可读
        while (in.isReadable()) {

            // 1. 记录最开始读取的位置 , 防止出错
            int release = in.readerIndex();

            // 2. 读取版本号 , 2个字节
            short version = in.readShort();

            // 3. 版本号一致 - > 继续执行
            if (version == VERSION) {

                // 4. 解码 -> 操作
                NPack read = null;
                byte[] bytes = null;

                try {

                    // 5. 读取长度 , 4个字节
                    int len = in.readInt();

                    // 6. 实例化数组
                    bytes = new byte[len];

                    // 7. 读取到数组中 , 此时可能会有异常 - > 我们抓住 indexOutOfBoundException
                    in.readBytes(bytes, 0, len);

                    // 8. 如果么问题, 就进行解码 -> 也可能出现异常 -> 抓取异常
                    read = pack.read(bytes, NPack.class);

                    // catch 抓取任何异常
                } catch (Throwable e) {
                    // 不做任何处理
                } finally {

                    // 清空数组引用 - 快速释放内存
                    bytes = null;
                }

                // 解码错误-> 重置读指针位置 -> 返回
                if (read == null) {
                    in.readerIndex(release);
                    return;
                } else {
                    // 一致就添加进去 - > 啥也不做
                    out.add(read);
                }
            } else {
                // 版本不一致 -> 重置读指针位置 -> 返回
                in.readerIndex(release);
                return;
            }
        }
    }


    /**
     * 测试用例
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


        // 初始化 pack
        MessagePack pack = new MessagePack();


        NPack nPack1 = NpackBuilder.buildWithJsonBody("1111", "22222", "3333");
        byte[] write1 = pack.write(nPack1);
        NPack nPack2 = NpackBuilder.buildWithJsonBody("BBBB", "CCCCC", "DDDD");
        byte[] write2 = pack.write(nPack2);


        // 模拟 in
        ByteBuf buffer = Unpooled.buffer(100);
        int release = buffer.readerIndex();

        buffer.writeShort(Constants.PROTOCOL_VERSION);
        buffer.writeInt(123);

        // 1. 写一个包
        buffer.writeShort(Constants.PROTOCOL_VERSION);
        buffer.writeInt(write1.length);
        buffer.writeBytes(write1, 0, write1.length);

        // 2. 写第二个包
        buffer.writeShort(Constants.PROTOCOL_VERSION);
        buffer.writeInt(write2.length);
        buffer.writeBytes(write2, 0, write1.length);


        // 3. 写可能出错的地方
        buffer.writeShort(Constants.PROTOCOL_VERSION);
        buffer.writeInt(1111);


        // 4. 重置
        buffer.readerIndex(release);


        ArrayList<Object> list = new ArrayList<>();


        PackageDecoder decoder = new PackageDecoder(Constants.PROTOCOL_VERSION);


        decoder.decode(null, buffer, list);


        System.out.println("=====已读======");
        list.forEach(System.out::println);
        System.out.println("==================");


        System.out.println("buffer.refCnt() = " + buffer.refCnt());
        System.out.println("buffer.writerIndex() = " + buffer.writerIndex());

        System.out.println("buffer.readerIndex() = " + buffer.readerIndex());

        System.out.println("buffer.readShort() = " + buffer.readShort());
        System.out.println("buffer.readInt() = " + buffer.readInt());

    }
}
