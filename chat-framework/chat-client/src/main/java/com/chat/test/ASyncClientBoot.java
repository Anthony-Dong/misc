package com.chat.test;

import com.chat.client.future.RpcProxy;
import com.chat.client.context.DefaultChatClientContext;
import com.chat.client.netty.AsyncChatClient;
import com.chat.core.exception.BootstrapException;
import com.chat.core.exception.ProxyException;
import com.chat.core.exception.TimeOutException;
import com.chat.core.inter.EchoService;
import com.chat.core.model.netty.Response;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * @date:2019/12/24 17:22
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ASyncClientBoot {

    public static void main(String[] args) throws Exception {
        DefaultChatClientContext clientContext = new DefaultChatClientContext();
        AsyncChatClient client = AsyncChatClient.run(9999, clientContext);

        Response response = clientContext.sendMessageBySync("hello world", "tom", "tony");
//        System.out.println(response.getUrl());

//        clientContext.sendMessage("hello world", "tom", "tony");


        clientContext.sendMessage("hello world", "tom", "tony");

//        client.close();

        EchoService service = RpcProxy.newInstance(EchoService.class, clientContext);
        Map<String, Object> echo2 = service.echo(Collections.singletonMap("name", "a"), Collections.singletonList("value"));
        System.out.println(echo2);
//        client.close();
    }

    private static void test(EchoService service) throws InterruptedException {
        assert service != null;
        TimeUnit.SECONDS.sleep(5);

        ExecutorService pool = Executors.newFixedThreadPool(10);
        long start = System.currentTimeMillis();
        IntStream.range(0, 10000).forEach(e -> {
            pool.execute(() -> {
                try {
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        });
        pool.shutdown();
        pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        System.out.printf("cost : %dms\n", System.currentTimeMillis() - start);
    }

    private static void testMessag() throws BootstrapException, ProxyException {


    }


    private static void testRpc() throws BootstrapException, InterruptedException {
        DefaultChatClientContext clientContext = new DefaultChatClientContext();
        AsyncChatClient client = AsyncChatClient.run(9999, clientContext);


        Method[] methods = EchoService.class.getMethods();
        Method method = methods[1];


        ExecutorService service = Executors.newFixedThreadPool(20);
        long start = System.currentTimeMillis();
        IntStream.range(0, 10000).forEach(value -> service.execute(() -> {
            Map<String, Object> map = new HashMap<>(Collections.singletonMap("name", value));
            List<String> list = new ArrayList<>(Collections.singletonList("value"));
            try {
                Object response = clientContext.invoke(EchoService.class, method, 1000, map, list);
                System.out.println("收到消息 : " + response + " , " + (System.currentTimeMillis() - start));
            } catch (TimeOutException e) {
                System.err.println(e.getMessage());
            }
        }));
        service.shutdown();
        service.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        System.out.println(System.currentTimeMillis() - start);
        client.close();
    }

    private static void testSyncMsg() throws Exception {
        DefaultChatClientContext clientContext = new DefaultChatClientContext();
        AsyncChatClient client = AsyncChatClient.run(9999, clientContext);
        IntStream.range(0, 1000).forEach(e -> {
            try {
                Response response = clientContext.sendMessageBySync("hello world", "tony", "tom");
                System.out.println(response);
            } catch (TimeOutException ee) {
                System.out.println(ee.getMessage());
            }
        });
        client.close();
    }


    private static void testAsyncMsg() throws Exception {
        DefaultChatClientContext clientContext = new DefaultChatClientContext();
        AsyncChatClient client = AsyncChatClient.run(9999, clientContext);
        IntStream.range(0, 1000).forEach(e -> {
            clientContext.sendMessage("hello world", "tony", "tom");
        });
        client.close();
    }
}
