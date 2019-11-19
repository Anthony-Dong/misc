package com.chat.core.model;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *  chat 的 threadFactory
 *
 * @date:2019/11/8 14:04
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ChatThreadFactory implements ThreadFactory {

    private AtomicInteger num = new AtomicInteger(0);

    private static ThreadGroup threadGroup;


    private String groupName;

    private boolean  daemon = false;


    public ChatThreadFactory(String groupName) {
        this.groupName = groupName;
        threadGroup = new ThreadGroup(groupName);
    }

    public ChatThreadFactory(String groupName, boolean daemon) {
        this.groupName = groupName;
        this.daemon = daemon;
        threadGroup = new ThreadGroup(groupName);
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = getThreadName();
        Thread thread = new Thread(threadGroup, r, name, 0);
        thread.setDaemon(daemon);
        return thread;
    }


    private String getThreadName() {
        return groupName + "-" + num.incrementAndGet();
    }

}
