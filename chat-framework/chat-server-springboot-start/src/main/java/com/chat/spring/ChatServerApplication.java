package com.chat.spring;


import com.chat.spring.annotation.EnableChatServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableChatServer
public class ChatServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("关闭");
        }));
    }
}
