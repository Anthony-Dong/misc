//package com.misc.core.register;
//
//import com.misc.core.exception.RegisterException;
//import com.misc.core.netty.common.PropertiesConstant;
//import com.misc.core.util.StringUtils;
//import org.slf4j.LoggerFactory;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//
//import java.net.InetSocketAddress;
//import java.net.SocketAddress;
//import java.util.*;
//
///**
// * @date:2020/2/16 14:02
// * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
// */
//public class RedisRegistryService implements RegistryService {
//
//    private final static org.slf4j.Logger Logger = LoggerFactory.getLogger(RedisRegistryService.class);
//
//
//    private final JedisPool jedisPool;
//
//    private final int EXPIRE_TIME;
//
//    // 根据系统属性获取,然后注入
//    public RedisRegistryService() {
//        String host = System.getProperty(PropertiesConstant.CLIENT_REGISTER_REDIS_HOST, "localhost");
//        Integer port = Integer.getInteger(PropertiesConstant.CLIENT_REGISTER_REDIS_PORT, 6379);
//        Logger.debug("Redis-Registry bind {}:{}.", host, port);
//        this.EXPIRE_TIME = Integer.getInteger(PropertiesConstant.CLIENT_REGISTER_HEART_TIME, 30000);
//        this.jedisPool = new JedisPool(host, port);
//    }
//
//
//    /**
//     * redis 存储 , 我们需要维护chat-server
//     */
//    @Override
//    public void register(SocketAddress address, short version) throws RegisterException {
//        Logger.debug("Redis-Registry register server address:{},version:{}.", address.toString(), version);
//
//        String REDIS_KEY = PropertiesConstant.CLIENT_REGISTER_KEY + "-" + version;
//
//        // 防止大量实例化数据
//        StringBuilder builder = new StringBuilder();
//
//        // 定时器
//        Timer timer = new Timer("redis-register", true);
//
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                try (Jedis jedis = jedisPool.getResource()) {
//                    StringBuilder append = builder.append(System.currentTimeMillis());
//                    // 初始化的时候将他插入进去
//                    jedis.hset(REDIS_KEY, address.toString(), append.toString());
//
//                    // 其余的时候不断检查 , 当超出30S,直接把他删了
//                    Map<String, String> map = jedis.hgetAll(REDIS_KEY);
//                    Set<Map.Entry<String, String>> set = map.entrySet();
//                    for (Map.Entry<String, String> entry : set) {
//                        String value = entry.getValue();
//                        String key = entry.getKey();
//                        if (System.currentTimeMillis() - Long.parseLong(value) > EXPIRE_TIME) {
//                            jedis.hdel(REDIS_KEY, key);
//                        }
//                    }
//                } finally {
//                    builder.setLength(0);
//                }
//            }
//        }, 0, EXPIRE_TIME);
//    }
//
//    @Override
//    public void unregister(SocketAddress address, short version) throws RegisterException {
//        String REDIS_KEY = PropertiesConstant.CLIENT_REGISTER_KEY + "-" + version;
//        try (Jedis redis = jedisPool.getResource()) {
//            redis.hdel(REDIS_KEY, address.toString());
//        }
//    }
//
//
//    @Override
//    public Set<InetSocketAddress> lookup(short version) throws RegisterException {
//        Jedis redis = jedisPool.getResource();
//
//        Map<String, String> map = redis.hgetAll(PropertiesConstant.CLIENT_REGISTER_KEY + "-" + version);
//
//        Set<String> set = map.keySet();
//
//        HashSet<InetSocketAddress> addresses = new HashSet<>(set.size());
//
//        set.forEach(e -> {
//            String[] split = StringUtils.split(e, ':');
//            if (split.length == 2) {
//                InetSocketAddress address = new InetSocketAddress(split[0], Integer.parseInt(split[1]));
//                addresses.add(address);
//            }
//        });
//        return addresses;
//    }
//}
