package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * TODO
 *
 * @date:2019/12/26 21:45
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class TestIO {
    public static void main(String[] args) throws Exception {
        RandomAccessFile file = new RandomAccessFile(new File("C:\\Users\\12986\\Desktop\\草稿本.txt"), "r");


        long length = file.length();

        byte[] bytes = new byte[(int) length];

        int read = file.read(bytes);

        System.out.println(read);



    }


}
