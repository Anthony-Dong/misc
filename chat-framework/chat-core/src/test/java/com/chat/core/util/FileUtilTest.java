package com.chat.core.util;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 分写测试
 */
public class FileUtilTest {

    @Test
    public void cuttingFile() throws Exception {


        long start1 = System.currentTimeMillis();

        File up = new File("D:\\MyDesktop\\文档\\数据库\\MySQL-5.7-en.pdf");

        List<byte[]> bytes = FileUtil.cuttingFile(up, FileUtil.LEN_1_MB);

        System.out.println("拆包 : " + bytes.size() + "次 , 耗时 : " + (System.currentTimeMillis() - start1) + " ms.");

        long start2 = System.currentTimeMillis();


        File down = new File("D:\\MyDesktop\\文档\\数据库\\MySQL-5.7-cn.pdf");


        bytes.forEach(e -> {
            try {
                FileUtil.mergingFile(down, e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        System.out.println("合并文件耗时 : " + (System.currentTimeMillis() - start2) + " ms.");

    }
}