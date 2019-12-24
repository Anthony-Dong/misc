package com.chat.spring.boot;


import com.chat.spring.annotation.EnableChatServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@EnableChatServer
@Slf4j
@SpringBootApplication
public class ServerApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();


        builder.sources(ServerApp.class)
                .bannerMode(Banner.Mode.OFF)
                .web(WebApplicationType.SERVLET)
                .run(args);

    }


    @Override
    public void run(String... args) {
        log.info("[服务器] 启动Spring-Boot成功 ...");
    }
}
