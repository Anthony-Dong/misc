package com.chat.test;

import com.chat.client.context.DefaultChatClientContext;
import com.chat.client.future.RpcProxy;
import com.chat.client.netty.ChatClient;
import com.chat.core.netty.SerializableType;
import com.chat.core.test.EchoService;

import java.io.File;
import java.util.stream.IntStream;


/**
 * TODO
 *
 * @date:2020/2/28 13:09
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientBoot {

    public static void main(String[] args) throws Exception {
        DefaultChatClientContext clientContext = new DefaultChatClientContext();
        clientContext.setSerializableType(SerializableType.MESSGAE_PACK_GZIP);
        ChatClient client = ChatClient.run(9999, clientContext);

        EchoService service = RpcProxy.newInstance(EchoService.class, clientContext);

        long start = System.currentTimeMillis();

        IntStream.range(0, 5000).forEach(value -> {
            Integer hash = service.hash("hello world");
            System.out.println(hash);
        });

        System.out.println(System.currentTimeMillis() - start);
        client.close();
    }

    private static void sendFile(DefaultChatClientContext clientContext) {
        long start = System.currentTimeMillis();
        String s = clientContext.sendFileSync(new File("D:\\樊浩东\\软件\\office2010.iso"));
        System.out.println(String.format("save path: %s , spend: %dms.", s, System.currentTimeMillis() - start));
    }
}
