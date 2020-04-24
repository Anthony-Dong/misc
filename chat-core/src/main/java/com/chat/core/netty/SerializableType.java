package com.chat.core.netty;

/**
 * 协议类型.
 *
 * @date:2020/2/28 12:34
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public enum SerializableType {

    /**
     * message_pack
     */
    MESSAGE_PACK(CodecType.MESSAGE_PACK),
    /**
     * 压缩
     */
    MESSGAE_PACK_GZIP(CodecType.GZIP_MESSAGE_PACK),
    /**
     * json
     */
    JSON(CodecType.JSON_PACK),

    /**
     * 直接基于字节数组走的. 很方便.
     */
    BYTE_ARRAY(CodecType.BYTE_PACK),

    PROTOBUF(CodecType.PROTOBUF),
    ;


    /**
     * 与 {@link CodecType}
     */
    byte code;

    SerializableType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

}
