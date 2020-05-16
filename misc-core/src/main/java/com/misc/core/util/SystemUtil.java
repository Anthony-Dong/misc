package com.misc.core.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class SystemUtil {

    /**
     * 获取当前jvm进程的pid
     */
    public static int getCurrentProcessPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        System.out.println("当前进程的标识为：" + name);
        int index = name.indexOf("@");
        return Integer.parseInt(name.substring(0, index));
    }
}
