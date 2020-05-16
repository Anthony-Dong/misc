package com.misc.core.proto.file;

import lombok.Getter;
import lombok.Setter;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * todo
 *
 * @date: 2020-05-11
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
//@Getter
//@Setter
public class FileChannelTask {

    /**
     * 文件流
     */
    private FileOutputStream fileOutputStream;


    /**
     * 文件管道
     */
    private FileChannel fileChannel;


    /**
     * 文件的更新操作时间
     */
    private long timestamp;


    /**
     * 文件信息
     */
    private String fileName;

}
