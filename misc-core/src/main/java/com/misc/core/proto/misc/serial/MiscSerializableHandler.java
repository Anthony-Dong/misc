package com.misc.core.proto.misc.serial;

import com.misc.core.exception.CodecException;
import com.misc.core.model.MiscPack;
import com.misc.core.proto.SerializableType;
import io.netty.buffer.ByteBuf;

/**
 * 解码处理器
 *
 * @date: 2020-05-10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface MiscSerializableHandler {
    /**
     * 编码，如果需要更多字节数，返回{@link SerializableType#NEED_MORE}
     */
    Object decode(ByteBuf in) throws CodecException;

    /**
     * 解码，严格控制长度
     */
    void encode(MiscPack msg, ByteBuf out) throws CodecException;

    MiscSerializableHandler DEFAULT_HANDLER = new MiscSerializableHandler() {
        @Override
        public Object decode(ByteBuf in) throws CodecException {
            throw new CodecException("Misc can not decode because of not reference type");
        }

        @Override
        public void encode(MiscPack msg, ByteBuf out) throws CodecException {
            throw new CodecException("Misc can not encode because of not reference type");
        }
    };
}
