package com.chat.core.annotation;

import java.lang.annotation.*;

/**
 * 代表可能为空  可以是方法返回值, 可以是参数
 *
 * @date:2019/12/26 20:44
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MayEmpty {


}
