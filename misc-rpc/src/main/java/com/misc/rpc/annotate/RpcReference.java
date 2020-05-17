package com.misc.rpc.annotate;

import java.lang.annotation.*;

/**
 * TODO
 *
 * @date:2020/2/17 15:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcReference {

    String version();

}
