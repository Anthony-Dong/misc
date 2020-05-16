package com.misc.server;

import com.misc.core.env.MiscProperties;
import com.misc.core.util.ThreadPool;
import com.misc.server.netty.MiscServer2;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ServerBoot3 {


    public static void main(String[] args) {
        new MiscServer2(new MiscProperties(), new ThreadPool());
    }
}
