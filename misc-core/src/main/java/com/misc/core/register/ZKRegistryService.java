//package com.misc.core.register;
//
//import com.misc.core.exception.RegisterException;
//import com.misc.core.netty.common.PropertiesConstant;
//import com.misc.core.util.StringUtils;
//import org.apache.zookeeper.*;
//import org.apache.zookeeper.data.Stat;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.SocketAddress;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
///**
// * @date:2020/2/18 18:11
// * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
// */
//public class ZKRegistryService implements RegistryService {
//
//    /**
//     * 注册 输入服务器端地址 和 版本号
//     */
//    @Override
//    public void register(SocketAddress address, short version) throws RegisterException {
//        try {
//            ZooKeeper zooKeeper = new ZooKeeper(System.getProperty(PropertiesConstant.CLIENT_REGISTER_ZK_URL), 1000, null);
//            String home = "/" + PropertiesConstant.CLIENT_REGISTER_KEY;
//            ifNotExistsWithCreate(zooKeeper, home);
//            String path = home + "/" + version;
//            ifNotExistsWithCreate(zooKeeper, path);
//            createEphemeralChild(zooKeeper, path, "/" + address.toString());
//        } catch (IOException | InterruptedException | KeeperException e) {
//            throw new RegisterException(e);
//        }
//    }
//
//    /**
//     * 取消注册,zk不需要,直接死机就可以了
//     */
//    @Override
//    public void unregister(SocketAddress address, short version) throws RegisterException {
//
//    }
//
//    /**
//     * 获取版本号的 服务器地址
//     */
//    @Override
//    public Set<InetSocketAddress> lookup(short version) throws RegisterException {
//        try {
//            ZooKeeper zooKeeper = new ZooKeeper(System.getProperty(PropertiesConstant.CLIENT_REGISTER_ZK_URL), 5000, null);
//
//            String path = "/" + PropertiesConstant.CLIENT_REGISTER_KEY + "/" + version;
//
//            Set<String> strings = get(zooKeeper, path);
//
//            Set<InetSocketAddress> addresses = new HashSet<>();
//
//            strings.forEach(s -> {
//                String[] split = StringUtils.split(s, ',');
//                if (split != null && split.length == 2) {
//                    InetSocketAddress address = new InetSocketAddress(split[0], Integer.parseInt(split[1]));
//                    addresses.add(address);
//                }
//            });
//            return addresses;
//        } catch (IOException | InterruptedException | KeeperException e) {
//            throw new RegisterException(e);
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        ZooKeeper zooKeeper = new ZooKeeper("192.168.58.131:2181", 5000, new Watcher() {
//            @Override
//            public void process(WatchedEvent watchedEvent) {
//                System.out.println(watchedEvent);
//            }
//        });
//        String path = ifNotExistsWithCreate(zooKeeper, "/server-node");
//
//        String s = ifNotExistsWithCreate(zooKeeper, "/server-node/1");
//        String localhost = createEphemeralChild(zooKeeper, "/server-node/1", "/localhost");
//        System.out.println(localhost);
//        createEphemeralChild(zooKeeper, "/server-node/1", "/111111");
//
//        createEphemeralChild(zooKeeper, "/server-node/1", "/222222");
//
//        createEphemeralChild(zooKeeper, "/server-node/1", "/333333");
//        get(zooKeeper, "/server-node/1");
//
//
//        TimeUnit.SECONDS.sleep(10);
//    }
//
//    public static Set<String> get(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {
//        List<String> children = zooKeeper.getChildren(path, false);
//        Set<String> set = new HashSet<>(children.size());
//        set.addAll(children);
//        return set;
//    }
//
//    public static boolean isExists(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {
//        Stat exists = zooKeeper.exists(path, null);
//        return exists != null;
//    }
//
//
//    private static String createEphemeralChild(ZooKeeper zooKeeper, String path, String child) throws KeeperException, InterruptedException {
//        return zooKeeper.create(path + child, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
//    }
//
//    /**
//     * 创建一个持久节点
//     */
//    private static String ifNotExistsWithCreate(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {
//        Stat exists = zooKeeper.exists(path, null);
//        if (exists == null) {
//            return zooKeeper.create(path, "HELLO WORLD".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        }
//        return null;
//    }
//}
