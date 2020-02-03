package com.chat.core.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件拆分合并
 *
 * @date:2019/12/27 17:53
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class FileUtil {
    /**
     * 常用的长度
     */
    public static final long LEN_1_KB = 1024 * 5;
    public static final long LEN_5_KB = 1024 * 5;
    public static final long LEN_10_KB = 1024 * 10;
    public static final long LEN_20_KB = 1024 * 20;
    public static final long LEN_1_MB = 1024 * 1024;
    public static final long MAX_LENGTH = 0x7fffffff;


    /**
     * 切文件
     *
     * @param fileName  文件
     * @param delimiter 分割大小
     * @return 字节数组
     * @throws Exception 异常
     */
    public static List<byte[]> cuttingFile(File fileName, long delimiter) throws Exception {
        if (delimiter > MAX_LENGTH) {
            throw new Exception("文件切割最大为 2^32-1");
        }
        // try - with - resource
        try (RandomAccessFile file = new RandomAccessFile(fileName, "r"); FileChannel channel = file.getChannel()) {
            // 总长度
            long size = file.length();

            // 需要拆多少个包 , 防止数组拷贝
            int block = (int) (size % delimiter == 0 ? size / delimiter : (size / delimiter) + 1);

            // 新建数组, 防止数组拷贝
            ArrayList<byte[]> list = new ArrayList<>(block);


            // 1. 起始位置
            long position = channel.position();

            // 2. 只有大于他才执行
            while (size > delimiter) {

                ByteBuffer buffer = ByteBuffer.allocate((int) delimiter);
                channel.read(buffer, position);

                // 我们采用的是堆内存 , 不是直接内存的原因是因为我们要做数组拷贝 , 没必要
                byte[] array = buffer.array();

                // 添加进去
                list.add(array);

                // size 每次减小
                size = size - delimiter;

                // 位置每次 增加
                position = position + delimiter;
            }

            // 最后一次绝对不满 / 一开始就小于
            ByteBuffer buffer = ByteBuffer.allocate((int) delimiter);

            channel.read(buffer, position);

            byte[] bytes = new byte[(int) size];
            buffer.flip();

            // 写到未满的数组里
            buffer.get(bytes);

            // 添加进去
            list.add(bytes);

            return list;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 合并文件
     *
     * @param fileName 文件路径
     * @param bytes    字节流
     * @throws Exception 中途异常
     */
    public static void mergingFile(File fileName, byte[] bytes) throws Exception {
        try (RandomAccessFile upload = new RandomAccessFile(fileName, "rw"); FileChannel channel = upload.getChannel()) {
            // 起始位置 - 总长度
            long length = upload.length();
            channel.position(length);

            // 写入
            upload.write(bytes);
        } catch (Exception e) {
            // 抛出异常
            throw e;
        }
    }
}
