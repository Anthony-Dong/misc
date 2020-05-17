package com.misc.rpc.core;

import com.misc.core.exception.TimeOutException;
import com.misc.rpc.client.FallBack;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * misc 的 rpc调用需要的
 *
 * @date:2020/2/17 10:48
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcFuture{
    /**
     * 来累计NPackFuture
     */
    private static final ConcurrentHashMap<String, RpcFuture> FUTURES = new ConcurrentHashMap<>();

    /**
     * 请求
     */
    private final RpcRequest request;

    /**
     * 锁
     */
    private final Lock lock = new ReentrantLock();

    /**
     * 防止线程之间差异
     */
    private volatile RpcResponse response;
    private final Condition done = lock.newCondition();


    /**
     * 为了使得数据准确性足够,我们会必须时间准确
     */
    public RpcFuture(RpcRequest request) {
        this.request = request;
        FUTURES.put(request.getKey(), this);
    }

    /**
     * 我懒得写那么多具体业务实现.
     */
    public static void received(RpcResponse response, FallBack fallBack) {
        // 这里取 . 防止这里被删除了/为啥这里删除? 因为他的最优是1次(不出现问题的情况下), 而get+remove是两次,效率低.
        RpcFuture future = FUTURES.remove(response.getKey());
        if (future == null) {
            // 如果已经移除了, 说明此事已经超时了, 我们提供fallback接口进行处理
            fallBack.fallback(response);
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

    public RpcResponse get(long timeout) throws TimeOutException {
        long start = System.currentTimeMillis();
        if ((System.currentTimeMillis() - start) > timeout) {
            throw new TimeOutException(String.format("Request timeout the package is: %s", this.request));
        }
        lock.lock();
        try {
            while (this.response == null) {
                // 这里加入超时一般不会出现死锁.
                done.await(timeout, TimeUnit.MILLISECONDS);
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
            FUTURES.remove(this.request.getKey());
            throw new TimeOutException(String.format("Request timeout : %s", this.request));
        } else {
            return this.response;
        }
    }
}
