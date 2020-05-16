package com.misc.spring.annotation;

import com.misc.spring.config.MiscServerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * EnableChatServer 开启 NettyServer
 *
 * @date:2019/11/10 18:41
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MiscServerConfiguration.class)
public @interface EnableChatServer {


}
