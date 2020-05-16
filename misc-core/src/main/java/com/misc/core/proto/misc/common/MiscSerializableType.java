package com.misc.core.proto.misc.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化类型.
 *
 * @date:2020/2/28 12:34
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public enum MiscSerializableType {

    /**
     * message_pack
     */
    MESSAGE_PACK((byte) 1, "message pack序列化"),
    /**
     * 压缩
     */
    MESSGAE_PACK_GZIP((byte) 2, "message pack和gizp序列化"),
    /**
     * json
     */
    JSON((byte) 3, "json 序列化"),

    /**
     * 直接基于文本走的. 很方便.
     */
    BYTE_ARRAY((byte) 4, "文本序列化"),

    /**
     * Java 的序列化
     */
    Java_Serializable((byte) 5, "Java 序列化"),


    /**
     * protobuf 序列化
     */
    PROTOBUF((byte) 6, "protobuf 序列化"),
    ;

    static Map<Byte, MiscSerializableType> map = new HashMap<>();

    static {
        map.put(MESSAGE_PACK.getCode(), MESSAGE_PACK);
        map.put(MESSGAE_PACK_GZIP.getCode(), MESSGAE_PACK_GZIP);
        map.put(JSON.getCode(), JSON);
        map.put(BYTE_ARRAY.getCode(), BYTE_ARRAY);
    }

    static byte[] SYSTEM_CODEC_TYPE = {
            1, 2, 3, 4, 5, 6
    };

    /**
     * 与 {@link MiscProtoConstance}
     */
    private byte code;

    private String info;

    MiscSerializableType(byte code, String info) {
        this.code = code;
        this.info = info;
    }

    public byte getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public static MiscSerializableType getType(byte type) {
        return map.get(type);
    }

    /**
     * 需要更多数据
     */
    public static final Object NEED_MORE = new Object();

}
