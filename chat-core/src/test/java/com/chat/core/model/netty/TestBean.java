package com.chat.core.model.netty;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * TODO
 *
 * @date:2020/2/17 17:31
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class TestBean {
    public String test(Map<String, Object> rest, List<String> list) {
        System.out.println(rest.getClass());

        System.out.println(list.getClass());

        rest.forEach(new BiConsumer<String, Object>() {
            @Override
            public void accept(String s, Object o) {
                System.out.println(s + " : " + o);
            }
        });

        list.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("index  : "+s);
            }
        });

        return "ok";
    }


    public void test3(){

    }


}
