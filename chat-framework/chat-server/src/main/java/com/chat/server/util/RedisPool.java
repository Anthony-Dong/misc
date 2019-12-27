package com.chat.server.util;


import com.chat.core.netty.Constants;
import com.chat.server.spi.SaveReceivePackage;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
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
            if (jedis != null) {
                queue.putConnection(jedis);
            }
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

    private static final String REDIS_HOST = "redis.host";
    private static final String REDIS_PORT = "redis.port";
    private static final String REDIS_POOL = "redis.pool.size";
    private static final String DEFAULT_HOST = "localhost";
    private static final String PROPERTIES_PATH_NAME = "chat-server.properties";


    /**
     * 连接Redis用的
     * 默认使用的是 系统属性,
     * 其次使用的是:  classpath: chat-server.properties
     *
     * @return RedisPool
     */
    public static RedisPool loadRedisPool() {
        // 属性
        String s_host = System.getProperty(REDIS_HOST, DEFAULT_HOST);
        String s_port = System.getProperty(REDIS_PORT);
        String s_size = System.getProperty(REDIS_POOL);
        if (null == s_port) {
            Properties properties = new Properties();
            try {
                properties.load(SaveReceivePackage.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String p_host = properties.getProperty(REDIS_HOST, DEFAULT_HOST);
            String p_port = properties.getProperty(REDIS_PORT);
            String p_size = properties.getProperty(REDIS_POOL);
            return new RedisPool(null == p_size ? 10 : Integer.parseInt(p_size.trim()), new HostAndPort(p_host, Integer.parseInt(p_port.trim())));
        } else {
            return new RedisPool(null == s_size ? 10 : Integer.parseInt(s_size.trim()), new HostAndPort(s_host, Integer.parseInt(s_port.trim())));
        }
    }


    public static String redisKeyName(InetSocketAddress address) {
        String hostName = address.getHostName();
        int port = address.getPort();
        return hostName + Constants.REDIS_KEY_DELIMITER + port;
    }
}
