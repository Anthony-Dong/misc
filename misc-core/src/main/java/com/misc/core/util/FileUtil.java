package com.misc.core.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 文件拆分与合并
 * <p>
 * 文件压缩
 * <p>
 * 数据压缩
 * <p>
 * 文件拷贝
 * <p>
 * 文件夹查找文件
 * <p>
 * 实时读取文件内容
 *
 * @date:2019/12/27 17:53
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class FileUtil {

    private FileUtil() {
    }

    /**
     * 常用的长度
     */
    public static final int LEN_1_KB = 1024 * 5;
    public static final int LEN_5_KB = 1024 * 5;
    public static final int LEN_10_KB = 1024 * 10;
    public static final int LEN_20_KB = 1024 * 20;
    public static final int LEN_1_MB = 1024 * 1024;


    /**
     * 切文件
     *
     * @param fileName  文件
     * @param delimiter 分割大小
     * @return 字节数组
     * @throws Exception 异常
     */
    public static List<byte[]> cuttingFile(File fileName, int delimiter) throws IOException {

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

                ByteBuffer buffer = ByteBuffer.allocate(delimiter);
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
            ByteBuffer buffer = ByteBuffer.allocate(delimiter);

            channel.read(buffer, position);

            byte[] bytes = new byte[(int) size];
            buffer.flip();

            // 写到未满的数组里
            buffer.get(bytes);

            // 添加进去
            list.add(bytes);

            return list;
        } catch (IOException e) {
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
    public static void mergingFile(File fileName, byte[] bytes) throws IOException {
        try (RandomAccessFile upload = new RandomAccessFile(fileName, "rw"); FileChannel channel = upload.getChannel()) {
            // 起始位置 - 总长度
            long length = upload.length();
            channel.position(length);

            // 写入
            upload.write(bytes);
        } catch (IOException e) {
            // 抛出异常
            throw e;
        }
    }


    /**
     * 实时读取文件
     */
    public static void consumeTimelyFile(final String fileName, Consumer<String> consumer) throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File(fileName), "r");
        FileChannel channel = file.getChannel();
        ByteBuffer allocate = ByteBuffer.allocate(128);
        // 开始长度
        int start = 0;

        while (true) {

            // 1. 清空
            allocate.clear();

            // 2. 读
            int read = channel.read(allocate, start);


            // 3. 如果读取数据为-1 返回
            if (read == -1) continue;


            // 4. start=start+读取长度
            start += read;


            // 变成数组 -> 由于需要读取不需要0拷贝
            byte[] array = allocate.array();


            // 5.读取日志
            String log = new String(array, 0, read, Charset.forName("utf8"));

            // 消费数据
            consumer.accept(log.trim());
        }
    }

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param dest   目的文件
     * @throws IOException 异常
     */
    public static void copyFile(String source, String dest) throws IOException {
        try (RandomAccessFile sourceFile = new RandomAccessFile(new File(source), "r");
             RandomAccessFile destFile = new RandomAccessFile(new File(dest), "rw");
             FileChannel destChannel = destFile.getChannel();
             FileChannel channel = sourceFile.getChannel()) {
            long length = sourceFile.length();
            channel.transferTo(0, length, destChannel);
        } catch (IOException e) {
            throw e;
        }
    }


    /**
     * 记录文件夹文件 , 递归实现, 也可以用栈来实现
     *
     * @param dir    文件夹
     * @param filter 过滤器
     * @param files  保存文件位置
     */
    public static void recordFile(File dir, Predicate<File> filter, List<File> files) {
        if (dir.isDirectory()) {
            File[] file = dir.listFiles();
            if (file == null || file.length == 0) {
                return;
            }
            for (File f : file) {
                recordFile(f, filter, files);
            }
        } else {
            if (filter.test(dir)) {
                files.add(dir);
            }
        }
    }


    /**
     * 记录文件夹文件 , 递归实现, 也可以用栈来实现,默认全部通过
     *
     * @param dir   文件夹
     * @param files 保存文件位置
     */
    public static void recordFile(File dir, List<File> files) {
        recordFile(dir, DEFAULT_FILTER, files);
    }

    public static final Predicate<File> DEFAULT_FILTER = file -> true;

    /**
     * SNAPPY 算法
     */
//    public static final Function<byte[], byte[]> SNAPPY_UNCOMPRESS = bytes -> {
//        try {
//            return Snappy.uncompress(bytes);
//        } catch (IOException e) {
//            throw new RuntimeException("转码错误");
//        }
//    };

//    public static final Function<byte[], byte[]> SNAPPY_COMPRESS = bytes -> {
//        try {
//            return Snappy.compress(bytes);
//        } catch (IOException e) {
//            throw new RuntimeException("转码错误");
//        }
//    };

    /**
     * GZIP 算法
     */
    public static final Function<byte[], byte[]> GZIP_COMPRESS = bytes -> {
        try {
            return gzip(bytes);
        } catch (IOException e) {
            throw new RuntimeException("转码错误");
        }
    };

    public static final Function<byte[], byte[]> GZIP_UNCOMPRESS = bytes -> {
        try {
            return unGzip(bytes);
        } catch (IOException e) {
            throw new RuntimeException("转码错误");
        }
    };


    /**
     * GZIP 解压缩算法
     * 读 为解压缩 . read()
     */
    public static byte[] unGzip(byte[] source) throws IOException {
        if (source == null) return null;
        byte[] result = null;
        GZIPInputStream gis = null;
        ByteArrayOutputStream baos = null;
        byte[] content = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(source);
            gis = new GZIPInputStream(bis);
            baos = new ByteArrayOutputStream();
            content = new byte[source.length * 2];
            int len = -1;
            while ((len = gis.read(content, 0, content.length)) != -1) {
                baos.write(content, 0, len);
            }
            baos.flush();
        } finally {
            if (gis != null) {
                gis.close();
            }
            if (baos != null) {
                result = baos.toByteArray();
            }
            content = null;
        }
        return result;
    }


    /**
     * GZIP 压缩算法
     * 写 为压缩(输出流). write ()
     */
    public static byte[] gzip(byte[] source) throws IOException {
        if (source == null) return null;
        byte[] result = null;
        ByteArrayOutputStream bos = null;
        GZIPOutputStream gzip = null;
        try {
            bos = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(bos);
            gzip.write(source);
        } finally {
            if (gzip != null) {
                gzip.close();
            }
            if (bos != null) {
                result = bos.toByteArray();
            }
        }
        return result;
    }


    /**
     * 解压缩文件
     */
    public static boolean unCompressFile(File src, File dest, Function<byte[], byte[]> unCompressHandler) throws IOException {
        if (dest != null && !dest.exists()) {
            boolean isOk = dest.createNewFile();
            if (!isOk) {
                throw new RuntimeException("创建文件错误");
            }
        }
        return handlerFile(src, dest, Integer.MAX_VALUE, unCompressHandler);
    }

    /**
     * 压缩文件
     */
    public static boolean compressFile(File src, File dest, Function<byte[], byte[]> compressHandler) throws IOException {
        if (dest != null && !dest.exists()) {
            boolean isOk = dest.createNewFile();
            if (!isOk) {
                throw new RuntimeException("创建文件错误");
            }
        }
        return handlerFile(src, dest, Integer.MAX_VALUE, compressHandler);
    }

    /**
     * 读取一个文件的文件流 . 也就是字节数组.
     * 处理完这些字节数组. 然后再写入到新的文件.
     */
    public static boolean handlerFile(File src, File dest, int fileSize, Function<byte[], byte[]> handler) throws IOException {
        if (!src.exists() || !dest.exists() || src.isDirectory() || dest.isDirectory()) {
            throw new RuntimeException("文件不存在 , 请检查");
        }
        RandomAccessFile srcFile = null;
        FileChannel channel = null;
        byte[] rest = null;
        ByteBuffer source = null;
        RandomAccessFile destFile = null;
        try {
            srcFile = new RandomAccessFile(src, "r");
            long length = srcFile.length();
            if (length > fileSize) {
                throw new RuntimeException("文件太大 , 超过了限制");
            }

            // 读
            channel = srcFile.getChannel();
            source = ByteBuffer.allocate((int) length);
            channel.read(source, 0);

            // 写
            rest = handler.apply(source.array());

            if (rest != null) {
                destFile = new RandomAccessFile(dest, "rw");
                destFile.write(rest);
                return true;
            } else {
                return false;
            }
        } finally {
            // release 操作
            if (srcFile != null) {
                srcFile.close();
            }
            if (channel != null) {
                channel.close();
            }
            if (destFile != null) {
                destFile.close();
            }
            rest = null;
            source = null;
        }
    }
}