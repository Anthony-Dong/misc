package com.chat.core.util;

import java.util.concurrent.*;

/**
 * 线程池
 *
 * @date:2020/2/3 17:04
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ThreadPool {

    /**
     * 线程池
     */
    private final ThreadPoolExecutor executor;

    /**
     * 线程数 . 大于等于0
     */
    private final int nThreads;

    /**
     * ThreadFactory - Name  , 其实就是 ThreadGroup的名字
     */
    private final String threadName;

    /**
     * 队列大小
     */
    private final int queues;

    /**
     * queue 小于0 为无界队列.
     */
    public ThreadPool(final int nThreads, final int queues, final String name) {
        this.nThreads = nThreads;
        this.queues = queues;
        this.threadName = name;
        this.executor = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                queues == 0 ? new SynchronousQueue<>() : (
                        queues < 0 ? new LinkedBlockingQueue<>()
                                : new LinkedBlockingQueue<>(queues)),
                new NamedThreadFactory(name, true),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * 执行任务
     */
    public void execute(Runnable task) {
        this.executor.execute(task);
    }

    /**
     * 获取活跃线程数
     */
    public int getActiveCount() {
        return this.executor.getActiveCount();
    }

    /**
     * 获取线程组名字
     */
    public String getThreadGroupName() {
        return threadName;
    }

    /**
     * 获取线程池大小
     */
    public int getPoolSize() {
        return this.nThreads;
    }

    /**
     * 获取任务队列大小
     */
    public int getBlockTaskSize() {
        return this.executor.getQueue().size();
    }

    /**
     * 获取线程大小
     */
    public int getQueueSize() {
        return this.queues;
    }

    public Executor getExecutor() {
        return this.executor;
    }
}
