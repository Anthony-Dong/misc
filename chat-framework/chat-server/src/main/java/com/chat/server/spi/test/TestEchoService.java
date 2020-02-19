package com.chat.server.spi.test;

import com.chat.core.annotation.Service;
import com.chat.core.inter.EchoService;

import java.util.List;
import java.util.Map;

/**
 * @date:2020/2/17 15:17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Service
public class TestEchoService implements EchoService {

    @Override
    public String echo() {
        return "ok :  成功调用了 : " + System.currentTimeMillis();
    }

    @Override
    public Map<String, Object> echo(Map<String, Object> msg, List<String> list) {
        System.out.println("map : " + msg.getClass() + " , " + " list :" + list.getClass());
        msg.put("list", list);
        return msg;
    }

}
