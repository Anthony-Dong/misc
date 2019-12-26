package com.chat.core.annotation;

import java.lang.annotation.*;

/**
 * 以协议的方式 申明改对象不允许为空, 使用权在于用户的手里
 *
 * @date:2019/12/25 10:11
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {

}
