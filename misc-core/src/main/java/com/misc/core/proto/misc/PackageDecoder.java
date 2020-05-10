package com.misc.core.proto.misc;


import com.misc.core.env.MiscProperties;
import com.misc.core.proto.misc.serial.MiscSerializableHandler;
import com.misc.core.commons.Constants;
import com.misc.core.proto.SerializableType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.misc.core.proto.misc.serial.MiscSerializableHandler.DEFAULT_HANDLER;
import static com.misc.core.proto.misc.common.MiscProtoConstance.*;
import static com.misc.core.commons.Constants.DEFAULT_SERVER_VERSION;
import static com.misc.core.commons.PropertiesConstant.CLIENT_SERVER_VERSION;

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

final class PackageDecoder extends ByteToMessageDecoder {

    /**
     * 默认值是 {@link Constants#DEFAULT_SERVER_VERSION}
     */
    private final short serverVersion;

    /**
     * 魔数(1) + 版本号(2) + 类型(1)
     */
    private static final int VERSION_AND_TYPE_LEN = 4;


    /**
     * 编解码处理器
     */
    private final Map<Byte, MiscSerializableHandler> serializeHandlerMap;

    /**
     * 构造方法
     */
    PackageDecoder(MiscProperties properties, Map<Byte, MiscSerializableHandler> serializeHandlerMap) {
        super();
        this.serverVersion = properties.getShort(CLIENT_SERVER_VERSION, DEFAULT_SERVER_VERSION);
        this.serializeHandlerMap = Objects.requireNonNull(serializeHandlerMap);
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
            // 保存点
            int save = in.readerIndex();

            // 判断魔数.
            byte magic = in.readByte();
            if (magic != MAGIC_NUMBER) {
                in.readerIndex(save);
                return;
            }

            // 获取版本号
            short version = in.readShort();
            if (version != serverVersion) {
                in.readerIndex(save);
                return;
            }
            //  编解码类型
            byte type = in.readByte();
            MiscSerializableHandler handler = serializeHandlerMap.getOrDefault(type, DEFAULT_HANDLER);
            Object res = handler.decode(in);
            // 需要更多则返回继续读
            if (res == SerializableType.NEED_MORE) {
                in.readerIndex(save);
                return;
            }
            // 添加进去
            out.add(res);
        }
    }
}
