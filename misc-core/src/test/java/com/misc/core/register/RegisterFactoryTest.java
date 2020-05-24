package com.misc.core.register;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegisterFactoryTest {

    @Test
    public void createRegistry() throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .namespace("node")
                .connectionTimeoutMs(1000)
                .connectString("127.0.0.1:2181")
                .retryPolicy(retryPolicy)
                .build();

        // 启动
        client.start();


        client
                .create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath("/test/testnode", "init".getBytes());


        List<String> test = client.getChildren().forPath("/test");
        test.forEach(System.out::println);

        TimeUnit.SECONDS.sleep(5);

        client.delete().deletingChildrenIfNeeded().forPath("/test");
    }
}