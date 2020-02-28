package com.chat.core.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.chat.core.netty.CodecType.*;

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
    private final short serverVersion;

    /**
     * 魔数(1) + 版本号(2) + 类型(1)
     */
    private static final int VERSION_AND_TYPE_LEN = 4;

    /**
     * 为了线程安全会保存一个私有对象
     */
    private final FileProtocol fileProtocol;

    /**
     * 构造方法
     */
    public PackageDecoder(short version, String dir) {
        super();
        this.serverVersion = version;
        this.fileProtocol = new FileProtocol(dir);
    }

    /**
     * {@link ByteToMessageDecoder#channelRead}
     * <p>
     * 解码器
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // version+type+magic 等于4
        while (in.readableBytes() > VERSION_AND_TYPE_LEN) {
            // 1.保存点
            int save = in.readerIndex();

            // 判断魔数.
            byte magic = in.readByte();
            if (magic != MAGIC_NUMBER) {
                in.readerIndex(save);
                return;
            }

            // 2. 获取版本号
            short version = in.readShort();
            if (version != serverVersion) {
                in.readerIndex(save);
                return;
            }
            // 3. 判断类型
            byte type = in.readByte();
            // 4. 以后修改这里.处理逻辑. switch . case 太过于拓展性差.
            switch (type) {
                // message_pack类型
                case MESSAGE_PACK:
                    Object msg = MessagePackProtocol.decode(in);
                    if (msg == CodecType.NEED_MORE) {
                        in.readerIndex(save);
                        return;
                    } else {
                        out.add(msg);
                        break;
                    }
                    // zip
                case GZIP_MESSAGE_PACK:
                    Object zip = MessagePackGzipProtocol.decode(in);
                    if (zip == CodecType.NEED_MORE) {
                        in.readerIndex(save);
                        return;
                    } else {
                        out.add(zip);
                        break;
                    }
                    // json
                case JSON_PACK:
                    Object json = JsonProtocol.decode(in);
                    if (json == CodecType.NEED_MORE) {
                        in.readerIndex(save);
                        return;
                    } else {
                        out.add(json);
                        break;
                    }
                    // 文件开始标识符
                case FILE_START:
                    if (fileProtocol.decodeFile(in)) {
                        break;
                    } else {
                        in.readerIndex(save);
                        return;
                    }
                    // 文件结束标识符
                case FILE_END:
                    if (fileProtocol.removeChannel(in, ctx)) {
                        break;
                    } else {
                        in.readerIndex(save);
                        return;
                    }
                default:
                    in.readerIndex(save);
                    handlerDefault(type);
                    return;
            }
        }
    }

    /**
     * 抛出异常,无法处理直接移除.
     */
    private void handlerDefault(byte type) {
        throw new RuntimeException(String.format("[服务器] 无法处理此类型:%d", type));
    }


    /**
     * Gets called after the {@link ByteToMessageDecoder} was removed from the actual context and it doesn't handle
     * events anymore.
     */
    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        try {
            fileProtocol.release();
        } finally {
            super.handlerRemoved0(ctx);
        }
    }
}
