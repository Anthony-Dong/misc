package com.misc.core.proto;

/**
 * 协议类型
 *
 * @date: 2020-05-10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public enum ProtocolType {

    MISC_PROTO((byte) 1, "misc"),
    FILE_PROTO((byte) 2, "file"),
    HTTP_PROTO((byte) 3, "http"),
    RPC_PROTO((byte) 4, "rpc"),
    ;

    private byte type;
    private String info;

    ProtocolType(byte type, String info) {
        this.type = type;
        this.info = info;
    }

    public byte getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }
}
