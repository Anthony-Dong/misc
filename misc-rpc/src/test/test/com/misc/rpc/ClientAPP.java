package com.misc.rpc;


import com.misc.core.loadbalance.LoadBalance;
import com.misc.core.register.ZKRegistryService;
import com.misc.core.test.EchoService;
import com.misc.rpc.client.ReferenceBean;
import com.misc.rpc.core.RpcProperties;
import com.misc.rpc.server.MiscRpcServer;
import com.misc.rpc.server.RpcServerConfig;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * 客户端 ， 最好开启debug日志模式·
 *
 * @date: 2020-05-10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientAPP {

    public static void main(String[] args) throws Exception {
        // 设置调用的次数，和请求的线程数(单机还是单线程好)
        int times = 10;
        int threads = 1;
        int clients = 1;

        // 设置远程调用方法的基本属性
        RpcProperties properties = new RpcProperties(EchoService.class.getMethod("hash", String.class));
        properties.setAck(true);
        properties.setFallBackClass(EchoServiceFallback.class);

        // 注册中心
        ZKRegistryService registry = new ZKRegistryService("localhost:2181");

        // 初始化 bean
        ReferenceBean<EchoService> referenceBean = new ReferenceBean<>(EchoService.class);
        referenceBean.setMethodPropertie(properties);
        referenceBean.setRegistryService(registry);
        List<EchoService> echoServices = initBean(referenceBean, clients);


        // rpc调用
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        long start = System.currentTimeMillis();
        IntStream.range(0, threads).forEach(value -> pool.execute(getJob(value * times, (value + 1) * times, echoServices, loadBalance)));
        pool.shutdown();
        pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        System.out.println(String.format("%d clients, %d threads, invoke %d times, spend %dms", clients, threads, times, System.currentTimeMillis() - start));
    }

    // task
    private static Runnable getJob(int start, int times, List<EchoService> echoServices, LoadBalance<EchoService> loadBalance) {
        return () -> IntStream.range(start, times).forEach(value -> {
            int[] hash = loadBalance.loadBalance(echoServices).hashCodes(1, "2", Arrays.asList(3));
            System.out.println(String.format("times=%d hashcode=%s", value, Arrays.toString(hash)));
        });
    }


    private static final LoadBalance<EchoService> loadBalance = new LoadBalance<EchoService>() {
        private Random random = new Random();

        @Override
        public EchoService loadBalance(List<EchoService> list) {
            int size = list.size();
            if (size == 0) {
                throw new NullPointerException("not be null");
            }
            int i = random.nextInt(size);
            return list.get(i);
        }
    };

    public static List<EchoService> initBean(ReferenceBean<EchoService> bean, int time) {
        List<EchoService> echoServices = new ArrayList<>(time);
        IntStream.range(0, time).forEach(value -> echoServices.add(bean.get()));
        return echoServices;
    }

    @Test
    public void runClient() {
        EchoService echoService = new ReferenceBean<>(EchoService.class, new ZKRegistryService()).get();
        int[] hash = echoService.hashCodes(1, "Hello Misc!", Collections.singletonList(1));
        System.out.printf("rpc invoke success , hash=%s", Arrays.toString(hash));
    }

    @Test
    public void runServer() throws Throwable {
        RpcServerConfig config = new RpcServerConfig();
        config.addInvoker(EchoService.class, (EchoService) (_int, _string, list) -> new int[]{_int, _string.hashCode(), list.hashCode()});
        MiscRpcServer.runSync(new ZKRegistryService(), config);
    }
}
