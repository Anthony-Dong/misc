package com.chat.core.netty;

/**
 * 定义了一些编解码需要的常量
 *
 * @date:2020/2/24 17:41
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface CodecType {

    /**
     * 魔数位
     */
    byte MAGIC_NUMBER = 0XF;

    /**
     * MSG-PACK
     */
    byte MESSAGE_PACK = 1;

    /**
     * MSG_PACK_ZIP
     */
    byte GZIP_MESSAGE_PACK = 2;

    /**
     * JSON 序列号
     */
    byte JSON_PACK = 3;


    byte BYTE_PACK = 4;

    byte PROTOBUF = 5;

    /**
     * 文件开始标志位
     */
    byte FILE_START = 126;

    /**
     * 文件结束标志位
     */
    byte FILE_END = 127;

    /**
     * 文件是否需要相应结果
     */
    byte FILE_NEED_ACK = 12;


    /**
     * 不需要相应结果
     */
    byte FILE_NOT_ACK = 13;


    /**
     * 需要更多数据
     */
    Object NEED_MORE = new Object();


}
