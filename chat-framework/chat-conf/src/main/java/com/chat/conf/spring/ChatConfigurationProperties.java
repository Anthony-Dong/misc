package com.chat.conf.spring;

import com.chat.conf.model.ConfConstant;
import com.chat.core.model.NServerInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;


/**
 * 配置服务的 基本信息
 * <p>
 * chat.conf.zookeeperIp=47.93.251.248:2181
 * chat.conf.zookeeperSessionTimeout=2000
 * chat.conf.zookeeperWatchPath=/chat
 * chat.conf.redisPoolSize=10
 * chat.conf.redisPort=2181
 * chat.conf.redisHost=47.93.251.248
 * chat.conf.pullScheduleInterval=60
 * chat.conf.pullScheduleSize=10
 *
 * @date:2019/11/12 19:05
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Slf4j
@Setter
@Getter
@ToString
@Component
@ConfigurationProperties(prefix = "chat.conf")
public class ChatConfigurationProperties implements InitializingBean {

    /**
     * {@link ConfConstant #NETTY_IP} ,存取每一个 server 节点的访问IP
     */
    private volatile ConcurrentHashMap<String, CopyOnWriteArrayList<String>> nettyServerInfo = new ConcurrentHashMap<>();

    /**
     * 优先队列 , 可以直接找出 最小值 , 初始化队列大小 ,可以自行调整 , 我没有选择这个主要是为了不麻烦 ,
     */
    private PriorityBlockingQueue<NServerInfo> queue = new PriorityBlockingQueue<NServerInfo>(10, new Comparator<NServerInfo>() {
        @Override
        public int compare(NServerInfo o1, NServerInfo o2) {
            return Integer.compare(o1.getTotalConnection(), o2.getTotalConnection());
        }
    });


    /**
     * 这个保存对象是用来去重的 ,是线程不安全的,这个玩意
     */
    private volatile HashSet<NServerInfo> ChatServerInfos = new HashSet<>();


    /**
     * ZK 配置
     */
    private String zookeeperIp;
    private int zookeeperSessionTimeout;
    private String zookeeperWatchPath = "/chat";
    private static final String WATCH_PATH_SUB = "chat-conf-watch-path";
    private ZooKeeper zooKeeper;


    /**
     * {@link ChatConfigScheduleListener } 的里面线程执行周期,默认是秒
     * <p>
     * 定时线程池大小
     */
    private Integer pullScheduleInterval = 60;
    /**
     * 定时线程池大小
     */
    private Integer pullScheduleSize = 10;

    /**
     * 在 bean 实例化完成后 关闭
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        // 默认 zk 是主线程启动 ,看需求改成新建一个线程启动

        zooKeeper = new ZooKeeper(this.zookeeperIp, this.zookeeperSessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 循环监听 子节点
                try {
                    getChild();
                } catch (KeeperException e) {
                    //
                } catch (InterruptedException e) {
                    //
                }
            }
        });

        log.info("[配置中心] 启动Zookeeper成功");

        //是否监听
        Stat exists = zooKeeper.exists(this.zookeeperWatchPath, true);
        if (null == exists) {
            zooKeeper.create(this.zookeeperWatchPath, WATCH_PATH_SUB.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    /**
     * 启动 zookeeper 的时候 对 节点进行不断的监控
     */
    private void getChild() throws KeeperException, InterruptedException {


        // 有变化 我们将服务器端信息清空
        ChatServerInfos.clear();

        // 获取子节点
        List<String> children = zooKeeper.getChildren(zookeeperWatchPath, true);

        // 将节点信息存入到 list中
        CopyOnWriteArrayList<String> uris = new CopyOnWriteArrayList<>();
        children.forEach(e -> {
            try {
                byte[] data = zooKeeper.getData(zookeeperWatchPath + "/" + e, null, null);
                String uri = new String(data);
                uris.add(uri);
            } catch (KeeperException e1) {
                //
            } catch (InterruptedException e1) {
                //
            }
        });

        log.info("[配置中心] 监听到节点变化 重新写入信息");

        // 保存在一个list集合中 , 覆盖这个是
        nettyServerInfo.put(ConfConstant.CHAT_SERVER_IP, uris);
    }
}
