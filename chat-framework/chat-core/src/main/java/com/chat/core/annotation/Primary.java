package com.chat.core.annotation;

import java.lang.annotation.*;

/**
 * SPI 优先级 , 越大优先级越高
 *
 * @date:2019/12/25 10:30
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Primary {

    /**
     * 越大优先级越高
     *
     * @return
     */
    int order() default Integer.MIN_VALUE;

}
