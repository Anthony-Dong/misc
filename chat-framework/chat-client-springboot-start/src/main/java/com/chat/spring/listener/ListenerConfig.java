package com.chat.spring.listener;


import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池配置
 *
 * @date:2019/11/13 17:10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
//@Configuration
public class ListenerConfig {

    /**
     * 异步线程池 , 一般没啥用
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor simpleAsyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(1024);
        executor.setKeepAliveSeconds(60);
        executor.setThreadFactory(new ChatThreadFactory("Chat-Client-Executor"));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

}
