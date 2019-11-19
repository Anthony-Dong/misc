package com.chat.spring.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;
/**
 *
 *
 * @date:2019/11/10 18:41
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ChatClientConfiguration.class)
public @interface EnableChatClient {



}
