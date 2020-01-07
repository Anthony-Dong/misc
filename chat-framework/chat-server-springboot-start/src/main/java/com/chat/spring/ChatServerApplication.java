package com.chat.spring;


import com.chat.server.handler.ChatServerContext;
import com.chat.server.netty.ChatServer;
import com.chat.spring.annotation.EnableChatServer;
import com.chat.spring.env.ChatServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableChatServer
public class ChatServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
    }

    @Autowired
    private ChatServerContext context;

    @Autowired
    private ChatServerProperties properties;

    @Override
    public void run(String... args) throws Exception {
        ChatServer.run(properties.getPort(), context);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("关闭");
        }));
    }
}
