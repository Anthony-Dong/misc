package com.misc.core.proto.file;

import com.misc.core.commons.Constants;
import com.misc.core.util.Pair;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 防止文件流造成泄漏
 *
 * @date: 2020-05-11
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class WheelingCleaningTask implements Runnable {

    /**
     * 清空的周期时间，多少ms做一次清空
     */
    private long clearFileChannelTime = Constants.DEFAULT_CLEAR_FILE_CHANNEL_TIME;


    /**
     * 最大的文件打开数字
     */
    private int openFileCount = Constants.DEFAULT_OPEN_FILE_COUNT;

    /**
     * 多线程操作，使用并发包map
     */
    private final Map<String, Pair<FileOutputStream, FileChannel>> fileChannelMap = new ConcurrentHashMap<>();


    /**
     * 添加任务，这里涉及到更新操作
     */
    public void addTask() {

    }


    /**
     * 删除任务，主要是主动发送完毕
     */
    public void removeTask() {

    }


    /**
     * 清空开启的文件
     */
    @Override
    public void run() {

    }
}
