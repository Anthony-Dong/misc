package com.misc.client;

import com.misc.client.context.DefaultMiscClientContext;
import com.misc.client.future.RpcProxy;
import com.misc.client.netty.MiscClient;
import com.misc.core.proto.SerializableType;
import com.misc.core.test.EchoService;

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
        DefaultMiscClientContext clientContext = new DefaultMiscClientContext();
        clientContext.setSerializableType(SerializableType.JSON);
        MiscClient client = MiscClient.run(9999, clientContext);

        EchoService service = RpcProxy.newInstance(EchoService.class, clientContext);

        long start = System.currentTimeMillis();

        IntStream.range(0, 10).forEach(value -> {
            Integer hash = service.hash("hello world");
            System.out.println(hash);
        });

        System.out.println(System.currentTimeMillis() - start);
        client.stop();
    }
}
