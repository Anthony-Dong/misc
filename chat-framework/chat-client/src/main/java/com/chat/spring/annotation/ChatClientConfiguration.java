package com.chat.spring.annotation;

import com.alibaba.fastjson.TypeReference;
import com.chat.core.listener.ChatBootSource;
import com.chat.spring.model.ChatClientProperties;
import com.chat.core.listener.ChatBootEvent;
import com.chat.core.listener.ChatBootListener;
import com.chat.client.core.ChatClient;
import com.chat.client.model.ChatTemplate;
import com.chat.core.model.NServerInfo;
import com.chat.core.util.HttpUtil;
import com.chat.core.util.JsonUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;



/**
 * 客户端启动配置中心
 *
 * @date:2019/11/10 18:42
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = {"com.chat.spring"})
public class ChatClientConfiguration {


    @Autowired
    private ChatClientProperties properties;


    /**
     * 聊天模板类
     * @return
     * @throws Exception
     */
    @Bean
    public ChatTemplate template() throws Exception {

        List<InetSocketAddress> connect = getConnect(properties.getConfigHttpUri());

        ChannelFuture[] futures = new ChannelFuture[connect.size()];

        ChatTemplate chatTemplate = null;
        for (int i = 0; i < connect.size(); i++) {
            ChatClient chatClient = initChatClient(connect.get(i));
            futures[i] = chatClient.getChannelFuture();
        }

        // 初始化放入 多个服务器节点, 防止 翻车
        chatTemplate = new ChatTemplate(futures);
        return chatTemplate;
    }


    /**
     * 初始化客户端
     *
     * @param inetSocketAddress
     * @return
     */
    public ChatClient initChatClient(InetSocketAddress inetSocketAddress) {

        ChatClient chatClient = null;

        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();


        chatClient = new ChatClient(eventLoopGroup,inetSocketAddress, new ChatBootListener() {
            @Override
            public void onChatBootEvent(ChatBootEvent event) {
                Object sou = event.getSource();
                ChatBootSource source = (ChatBootSource) sou;
                if (source.isSuccess()) {
                    log.info("[客户端] 启动成功  IP : {} ", inetSocketAddress);
                } else if (source.isFailed()) {
                    log.info("[客户端] 启动失败  IP : {} ", inetSocketAddress);
                } else if (source.isShutDown()) {
                    if (null != eventLoopGroup) {
                        eventLoopGroup.shutdownGracefully();
                    }
                    // 其实还应该有其他处理
                    log.info("[客户端] 成功关闭  IP : {} ", inetSocketAddress);
                }
            }
        });
        try {
            chatClient.start();
        } catch (Exception e) {
            // 关闭当前的 channel对象
            if (null != chatClient.getChannelFuture()) {
                chatClient.getChannelFuture().channel().closeFuture();
            }
            // 失败直接 shutdown
            chatClient.shutDown();
        }
        return chatClient;
    }


    /**
     * 向配置中心拉去信息
     *
     * @param confURI
     * @return
     * @throws IOException
     */
    public List<InetSocketAddress> getConnect(String confURI) throws IOException {
        String json = HttpUtil.doGet(confURI);
        List<NServerInfo> nServerInfos = JsonUtil.parseObject(json, new TypeReference<List<NServerInfo>>() {
        });

        List<InetSocketAddress> addresses = new CopyOnWriteArrayList<>();

        nServerInfos.forEach(e -> {
            addresses.add(new InetSocketAddress(e.getHost(), e.getPort()));
        });
        return addresses;
    }

}
