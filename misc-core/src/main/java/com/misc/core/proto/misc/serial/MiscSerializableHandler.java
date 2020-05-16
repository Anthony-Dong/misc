package com.misc.core.proto.misc.serial;

import com.misc.core.exception.CodecException;
import com.misc.core.model.MiscPack;
import com.misc.core.proto.misc.common.MiscSerializableType;
import com.misc.core.util.ExceptionUtils;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * 解码处理器
 *
 * @date: 2020-05-10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface MiscSerializableHandler {
    /**
     * 编码，如果需要更多字节数，返回{@link MiscSerializableType#NEED_MORE}
     */
    Object decode(ByteBuf in) throws CodecException;

    /**
     * 解码，严格控制长度
     */
    void encode(MiscPack msg, ByteBuf out) throws CodecException;


    MiscSerializableHandler DEFAULT_HANDLER = new MiscSerializableHandler() {
        @Override
        public Object decode(ByteBuf in) throws CodecException {
            throw new RuntimeException("MiscSerializableHandler 无法序列化");
        }

        @Override
        public void encode(MiscPack msg, ByteBuf out) throws CodecException {
            throw new RuntimeException("MiscSerializableHandler 无法序列化");
        }
    };


    static void initSerializeHandleMap(Map<Byte, MiscSerializableHandler> codecHandlerMap) {
        if (codecHandlerMap == null) {
            throw ExceptionUtils.newNullPointerException("Map<Byte, MiscSerializableHandler> 为空");
        }

        codecHandlerMap.put(MiscSerializableType.MESSAGE_PACK.getCode(), new MessagePackSerializableType());
        codecHandlerMap.put(MiscSerializableType.MESSGAE_PACK_GZIP.getCode(), new GzipMessagePackSerializableType());
        codecHandlerMap.put(MiscSerializableType.JSON.getCode(), new JsonSerializableType());
        codecHandlerMap.put(MiscSerializableType.BYTE_ARRAY.getCode(), new ByteSerializableType());
    }

}
