package com.chat.test;

import com.chat.client.context.DefaultChatClientContext;
import com.chat.client.future.RpcProxy;
import com.chat.client.netty.ChatClient;
import com.chat.core.exception.BootstrapException;
import com.chat.core.exception.ProxyException;
import com.chat.core.exception.TimeOutException;
import com.chat.core.model.netty.Response;
import com.chat.core.netty.CodecType;
import com.chat.core.netty.SerializableType;
import com.chat.core.test.EchoService;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static com.chat.core.netty.Constants.*;

/**
 * @date:2019/12/24 17:22
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ASyncClientBoot {

    public static void main(String[] args) throws Exception {
        DefaultChatClientContext clientContext = new DefaultChatClientContext();
        clientContext.setSerializableType(SerializableType.MESSGAE_PACK_GZIP);
        ChatClient client = ChatClient.run(9999, clientContext);

        EchoService service = RpcProxy.newInstance(EchoService.class, clientContext);
        IntStream.range(0, 10).forEach(value -> {
            Integer hash = service.hash("hello rpc");
            System.out.println(hash);
        });
        client.close();
    }


    private static void testFile() throws BootstrapException, IOException, ProxyException {
        DefaultChatClientContext clientContext = new DefaultChatClientContext();
        ChatClient client = ChatClient.run(9999, clientContext);
        client.close();
    }


    @Test
    public void test2() throws Exception {
        FileOutputStream stream = new FileOutputStream("D:\\代码库\\分布式聊天框架\\chat-framework\\test.txt");
        FileChannel channel = stream.getChannel();
        long start = channel.position();
        System.out.println(start);
        ByteBuffer allocate = ByteBuffer.allocate(10);
        String ok = "hello";
        allocate.put(ok.getBytes());
        allocate.flip();
        System.out.println(allocate);
        channel.write(allocate);
        System.out.println(allocate);
        //
        allocate.flip();
        channel.write(allocate, channel.position());
        System.out.println(channel.position());
    }

    @Test
    public void testPro() throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(DEFAULT_FILE_DIR + FILE_SEPARATOR + "a.txt");

    }


    private static void test3() throws Exception {
        DefaultChatClientContext clientContext = new DefaultChatClientContext();
        ChatClient client = ChatClient.run(9999, clientContext);
        EchoService service = RpcProxy.newInstance(EchoService.class, clientContext);
        assert service != null;
        ExecutorService pool = Executors.newFixedThreadPool(10);
        long start = System.currentTimeMillis();
        IntStream.range(0, 10).forEach(value -> {
            pool.execute(() -> IntStream.range(0, 1000).forEach(v -> {
//                String hash = service.hash(1111);
//                System.out.println(hash);
            }));
        });
        pool.shutdown();
        pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        System.out.println(String.format("耗时 : %dms.", System.currentTimeMillis() - start));
        client.close();
    }

    private static void test() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        long start = System.currentTimeMillis();
        IntStream.range(0, 10).forEach(e -> pool.execute(() -> {
            try {
                test3();
            } catch (Exception e1) {

            }
        }));
        pool.shutdown();
        pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        System.out.printf("cost : %dms\n", System.currentTimeMillis() - start);
    }

    private static void testMessag() throws BootstrapException, ProxyException {


    }


    private static void testRpc() throws BootstrapException, InterruptedException {
        DefaultChatClientContext clientContext = new DefaultChatClientContext();
        ChatClient client = ChatClient.run(9999, clientContext);


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
        ChatClient client = ChatClient.run(9999, clientContext);
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
        ChatClient client = ChatClient.run(9999, clientContext);
        IntStream.range(0, 1000).forEach(e -> clientContext.sendMessage("hello world", "tony", "tom"));
        client.close();
    }
}
