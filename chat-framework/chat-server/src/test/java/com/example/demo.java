package com.example;



import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * TODO
 *
 * @date:2019/12/26 17:39
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class demo {

    public static void main(String[] args) throws Exception {
        RandomAccessFile upload = new RandomAccessFile("C:\\Users\\12986\\Desktop\\file1.txt", "rw");
        long length = upload.length();

        FileChannel channel = upload.getChannel();
        channel.position(length);
        upload.write(new String("大小").getBytes());

    }


}
