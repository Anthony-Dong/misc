package com.chat.client.future;

import com.chat.core.exception.TimeOutException;
import com.chat.core.model.*;
import com.chat.core.model.netty.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @date:2020/2/17 10:48
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class NpackFuture {
    /**
     * 来累计NPackFuture
     */
    private static final ConcurrentHashMap<Integer, NpackFuture> FUTURES = new ConcurrentHashMap<>();

    /**
     * 单机
     */
    private static final AtomicInteger count = new AtomicInteger();

    private final int id;
    private final long timeout;
    private final NPack pack;
    private final Lock lock = new ReentrantLock();

    /**
     * 防止线程之间差异
     */
    private volatile Response response;
    private final Condition done = lock.newCondition();

    /**
     * 全局唯一的计数器,一台机器最好一个,一般不会出现问题,小排量的话,麻烦了.改成long类型即刻
     */
    public static int getCount() {
        return count.incrementAndGet();
    }

    /**
     * 为了使得数据准确性足够,我们会必须时间准确
     */
    public NpackFuture(int id, long timeout, NPack pack) {
        this.id = id;
        this.timeout = timeout;
        this.pack = pack;
        FUTURES.put(id, this);
    }

    /**
     * 我懒得写那么多具体业务实现.
     */
    public static void received(Response response, Consumer<Response> fallBack) {
        switch (response.getProtocol()) {
            case UrlConstants.MSG_PROTOCOL:
                receive(response, fallBack);
                break;
            case UrlConstants.RPC_PROTOCOL:
                receive(response, fallBack);
                break;
            case UrlConstants.FILE_PROTOCOL:
                receive(response, fallBack);
                break;
            default:
                fallBack.accept(response);
                break;
        }
    }

    private static void receive(Response response, Consumer<Response> fallBack) {
        String s = response.getId();
        // 这里取 . 防止这里被删除了/为啥这里删除? 因为他的最优是1次(不出现问题的情况下), 而get+remove是两次,效率低.
        NpackFuture future = FUTURES.remove(Integer.parseInt(s));
        if (future == null) {
            // 如果已经移除了, 说明此事已经超时了, 我们提供fallback接口进行处理
            fallBack.accept(response);
            return;
        }
        future.lock.lock();
        try {
            future.response = response;
            future.done.signal();
        } finally {
            future.lock.unlock();
        }
    }

    public Response get() throws TimeOutException {
        long start = System.currentTimeMillis();
        if ((System.currentTimeMillis() - start) > this.timeout) {
            throw new TimeOutException(String.format("请求超时,无法处理 : %s", this.pack));
        }
        lock.lock();
        try {
            while (this.response == null) {
                // 这里加入超时一般不会出现死锁.
                done.await(this.timeout, TimeUnit.MILLISECONDS);
                // 超时或者拿到则抛出
                if (this.response != null || System.currentTimeMillis() - start > timeout) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        // 双重检测,如果还没有, 我们代表已经超时了, 就移除,防止内存泄漏
        if (this.response == null) {
            // 如果还是没有结果, 就移除
            FUTURES.remove(this.id);
            throw new TimeOutException(String.format("请求超时,无法处理 pack: %s", this.pack));
        } else {
            return this.response;
        }
    }

    public void handlerFile(){

    }
}
