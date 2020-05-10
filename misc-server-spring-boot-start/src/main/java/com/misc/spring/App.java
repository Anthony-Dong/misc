package com.misc.spring;


import com.misc.spring.annotation.EnableChatServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableChatServer
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("关闭");
        }));
    }
}
