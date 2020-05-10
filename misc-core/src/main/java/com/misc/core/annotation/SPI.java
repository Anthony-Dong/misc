package com.misc.core.annotation;

import java.lang.annotation.*;

/**
 * 我框架中使用的SPI接口 - 都申明了此注解
 *
 * @date:2019/12/26 20:11
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPI {

    String value() default "";
}
