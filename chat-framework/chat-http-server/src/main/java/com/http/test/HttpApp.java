package com.http.test;

import com.http.netty.HttpServer;

import java.net.InetSocketAddress;

/**
 * TODO
 *
 * @date:2019/12/25 12:50
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class HttpApp {

    public static void main(String[] args) {
        try {
            HttpServer.run(8888);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
