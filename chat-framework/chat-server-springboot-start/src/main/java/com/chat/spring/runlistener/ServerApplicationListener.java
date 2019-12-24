package com.chat.spring.runlistener;


import com.chat.core.util.Snowflake;
import com.chat.server.netty.ChatServer;
import com.chat.server.util.RedisPool;
import com.chat.spring.model.ServerConfigs;
import com.chat.spring.web.ChatServerController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;


/**
 * 当 spring服务器启动完成后 , 回去加载完成所有的 bean  , 我们可以获取 {@link ServerConfigs}
 * <p>
 * <p>
 * 此时启动 core 服务器端
 *
 * @date:2019/11/12 19:01
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Slf4j
@Component
public class ServerApplicationListener implements ApplicationListener<ContextRefreshedEvent> {


    @Autowired
    private ServerConfigs configs;


    /**
     * 分布式 唯一ID
     */
    private static final Snowflake SNOWFLAKE = Snowflake.of();

    /**
     * 处理器
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        // 一个前缀 ,  http://134.175.154.93:6677/chat/server/info?nip=134.175.154.93:6666
        String pre = configs.getHttpPrefix() + configs.getHttpHost() + ":" + configs.getHttpPort() + ChatServerController.path;

        // redis  连接池
        RedisPool redisPool = new RedisPool(configs.getPoolMax(), new HostAndPort(configs.getRedisHost(), configs.getRedisPort()));

        // netty  的 host
        String socketHost = configs.getSocketHost();

        // 获取所有的 core 的 port
        List<Integer> socketPort = configs.getSocketPort();

        // 根据具体的端口号 获取大小 , 然后创建线程 , 开启多个客户端
        int size = socketPort.size();


        //ExecutorService executor = Executors.newFixedThreadPool(size);

        IntStream.range(0, size).forEach((e) -> {

            // 服务器端口号
            Integer nettyPort = socketPort.get(e);

            // ip
            String nettyIp = socketHost + ":" + nettyPort;

            // 获取计数器 的 map   , IP : 计数器
            HashMap<String, AtomicInteger> countMap = configs.getCountMap();

            // 根据 IP 获取计数器
            AtomicInteger counter = countMap.get(nettyIp);


            // 设置 netty的地址
            InetSocketAddress socketAddress = new InetSocketAddress(socketHost, nettyPort);


            // 创建一个客户端
            ChatServer chatServer = new ChatServer(socketAddress, null);
            // 启动客户端
            try {
                chatServer.start();
            } catch (Exception e1) {
                log.info("[服务器-{}] 发生异常 , 信息 :  ", e1.getMessage());
            }
        });
    }

    /**
     * 启动一个 zk 客户端
     *
     * @param key
     * @param value
     */
    private ZooKeeper bootZookeeper(String key, String value) throws IOException, KeeperException, InterruptedException {
        // 将节点注册到 zookeeper 上 , 注册一个临时节点 , 当这个客户端挂掉一后 ,这个临时节点就消失了
        //必须这么做 , 通过一个客户端 来实现
        ZooKeeper zooKeeper = new ZooKeeper(configs.getZookeeperIp(), configs.getZookeeperTimeout(), new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                log.info("[服务器] Zookeeper 临时节点启动成功 : {}", event.toString());
            }
        });
        // 创建一个 临时节点 , key是 唯一ID  , value 是 请求地址
        zooKeeper.create(key, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        return zooKeeper;
    }
}
