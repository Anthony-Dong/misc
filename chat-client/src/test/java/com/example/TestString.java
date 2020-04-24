package com.example;


import com.chat.core.netty.Constants;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * TODO
 *
 * @date:2020/2/28 15:19
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class TestString {

    public static FileOutputStream getStream(String dir, String fileName) {
        String trim = fileName.trim();
        int file = fileName.indexOf(0);

        char c = fileName.charAt(0);


        Character character = Constants.FILE_SEPARATOR.charAt(0);

        System.out.println(character);

        System.out.println(c);

        return null;


    }


    public static void main(String[] args) throws IOException {

        FileOutputStream hell = getStream("hell", "\\ok\\aaa");




//        FileOutputStream stream = null;
//        try {
//            stream = new FileOutputStream("D:\\代码库\\分布式聊天框架\\chat-framework\\test\\a.txt");
//        } catch (FileNotFoundException e) {
//
//            File file = new File("D:\\代码库\\分布式聊天框架\\chat-framework\\test");
//            if (!file.exists()) {
//                file.mkdir();
//                stream = new FileOutputStream("D:\\代码库\\分布式聊天框架\\chat-framework\\test\\a.txt");
//            }
//
//
//        }
//
//
//        FileChannel channel = stream.getChannel();

//
//        channel.write(ByteBuffer.wrap("nhello world".getBytes()));
//
//        channel.close();
//        stream.close();


        byte value = Byte.MAX_VALUE;

        System.out.println(value);

        String string = new String("hello world / test.file");

        System.out.println(string.getBytes().length);


    }


}
