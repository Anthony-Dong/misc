package com.chat.spring.boot;


import com.chat.spring.annotation.EnableChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableChatClient
@SpringBootApplication
public class ClientApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ClientApp.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

    }
}
