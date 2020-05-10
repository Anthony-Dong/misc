package com.misc.spring.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * 客户端启动配置中心
 *
 * @date:2019/11/10 18:42
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = {"com.misc.spring"})
public class ChatClientConfiguration {


}
