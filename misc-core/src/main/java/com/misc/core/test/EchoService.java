package com.misc.core.test;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date:2020/2/17 15:16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface EchoService {
    int[] hash(String str);

    default List<User> getUses(HashMap<String, String> value) {
        return null;
    }
}
