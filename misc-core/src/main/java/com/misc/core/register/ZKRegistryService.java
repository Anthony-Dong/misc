package com.misc.core.register;


import com.misc.core.exception.RegisterException;
import com.misc.core.model.URL;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * zk 简单的客户端
 *
 * @date:2020/2/18 18:11
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ZKRegistryService implements RegistryService {

    private static final String parent = "misc";

    private static final String path = "/url";

    private static final String defaultAddr = "localhost:2181";
    private String address = defaultAddr;

    public void setAddresss(String address) {
        this.address = address;
    }

    public ZKRegistryService(String address) {
        this.address = address;
    }

    public ZKRegistryService() {
    }

    @Override
    public void register(RemoteInfo info) throws RegisterException {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .namespace(parent)
                .connectionTimeoutMs(1000)
                .sessionTimeoutMs(5000)
                .connectString(address)
                .retryPolicy(retryPolicy)
                .build();
        client.start();

        String nodePath = buildPath(info.toUrl());
        Stat stat = null;
        try {
            stat = client.checkExists().creatingParentsIfNeeded().forPath(nodePath);
        } catch (Exception e) {
            throw new RegisterException(e);
        }

        // 存在 ，删除
        if (stat != null) {
            try {
                client.delete().deletingChildrenIfNeeded().forPath(nodePath);
            } catch (Exception e) {
                throw new RegisterException(e);
            }
        }


        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(nodePath, null);
        } catch (Exception e) {
            throw new RegisterException(e);
        }
    }

    @Override
    public Set<RemoteInfo> lookup(RemoteInfo info) throws RegisterException {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .namespace(parent)
                .connectionTimeoutMs(1000)
                .sessionTimeoutMs(5000)
                .connectString(address)
                .retryPolicy(retryPolicy)
                .build();

        client.start();
        try {
            List<String> strings = client.getChildren().forPath(path);
            Set<RemoteInfo> remoteInfos = new HashSet<>();
            strings.forEach(s -> {
                URL url = URL.valueOfByDecode(s);
                remoteInfos.add(RemoteInfo.makeInfo(url));
            });
            return remoteInfos;
        } catch (Exception e) {
            throw new RegisterException(e);
        }
    }

    private static String buildPath(URL url) {
        String encode = URL.encode(url.toString());
        return path + "/" + encode;
    }
}
