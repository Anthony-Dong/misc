package com.chat.core.inter;

import java.util.List;
import java.util.Map;

/**
 * @date:2020/2/17 15:16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface EchoService {
    String echo();

    Map<String,Object> echo(Map<String,Object> msg, List<String> list);

    default void test(){
        System.out.println("test");
    }
}
