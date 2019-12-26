package com.chat.server.util;


import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

/**
 * 由于 redis' 的部分原因 ,依赖的问题我们将redis 放在了 client 端
 *
 * @date:2019/11/11 12:01
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class RedisPool {

    private final ThreadLocal<Jedis> threadLocal;

    private final RedisConnectionQueue queue;


    public RedisPool(Integer capacity, HostAndPort hostAndPort) {
        this.queue = new RedisConnectionQueue(capacity, hostAndPort);
        this.threadLocal = new ThreadLocal<>();
    }


    public Jedis get() {
        if (null == threadLocal.get()) {
            threadLocal.set(queue.takeConnection());
        }
        return threadLocal.get();
    }


    public void remove(Jedis jedis) {
        if (null != threadLocal.get()) {
            threadLocal.remove();
            queue.putConnection(jedis);
        }
    }

    static class RedisConnectionQueue {

        private Boolean useFlag;

        private BlockingQueue<Jedis> queue;


        RedisConnectionQueue(final Integer capacity, final HostAndPort hostAndPort) {
            this.queue = new ArrayBlockingQueue<>(capacity);
            IntStream.range(0, capacity).forEach(e -> this.queue.offer(new Jedis(hostAndPort)));
        }

        Jedis takeConnection() {
            Jedis connection = null;
            try {
                connection = queue.take();
                return connection;
            } catch (InterruptedException e) {
                return null;
            }
        }

        void putConnection(Jedis connection) {
            try {
                queue.put(connection);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
