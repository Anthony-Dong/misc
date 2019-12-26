package com.chat.core.netty;

import com.chat.core.model.NPack;

/**
 * NPack 的一些使用属性
 *
 * @date:2019/11/10 13:47
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public final class Constants {

    //协议版本号
    public static final short PROTOCOL_VERSION = 1;


    //头部的长度： 版本号 + 报文长度
    public static final int PROTOCOL_HEAD_LENGTH = 6;


    //长度的偏移 , 从第二个字节开始
    public static final short LENGTH_OFFSET = 2;


    //长度的字节数 , 长度占多少字节
    public static final int LENGTH_BYTES_COUNT = 4;


    // 每帧的字节长度最多 1024*2  够上千个汉字了
    public static final short MAX_FRAME_LENGTH = 1024 * 2;


    // 心跳检测使用的
    public static final String HEART_BEAT_NPACK_ROUTER = "heart";

    // 默认的心跳包
    public static final NPack HEART_BEAT_NPACK = new NPack(HEART_BEAT_NPACK_ROUTER);

}
