package com.misc.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * TODO
 *
 * @date:2019/12/27 23:36
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Test {


    public static void main(String[] args) throws IOException {
        // 创建系统进程
        ProcessBuilder pb = new ProcessBuilder("tasklist");
        Process p = pb.start();
        BufferedReader out = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream()), Charset.forName("utf-8")));

        BufferedReader err = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getErrorStream())));
        System.out.println("Window 系统进程列表");
        String ostr;

        while ((ostr = out.readLine()) != null)
            System.out.println(ostr);
        String estr = err.readLine();
        if (estr != null) {
            System.out.println("\nError Info");
            System.out.println(estr);
        }
    }


}
