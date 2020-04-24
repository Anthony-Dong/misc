package com.chat.conf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * TODO
 *
 * @date:2019/11/12 19:12
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@EnableAsync
@SpringBootApplication
public class ConfApp {

    public static void main(String[] args) {
        SpringApplication.run(ConfApp.class, args);

    }

}
