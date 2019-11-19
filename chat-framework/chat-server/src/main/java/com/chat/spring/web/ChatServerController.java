package com.chat.spring.web;

import com.chat.core.exception.ChatHttpRequestException;
import com.chat.core.model.NServerInfo;
import com.chat.spring.runlistener.ServerApplicationListener;
import com.chat.spring.model.ServerConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * server 的服务中心
 * @date:2019/11/10 19:48
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@RestController
@RequestMapping("/chat/server")
public class ChatServerController {

    @Autowired
    private ServerConfigs configs;


    // 这个必须和 map 对应起来 , 其他地方依赖
    /**
     * {@link ServerApplicationListener}
     */
    public static final String path = "/chat/server/info";

    /**
     * 获取 信息  http://127.0.0.1:8088/chat/server/info?ip=127.0.0.1:8885
     *
     * @param ip
     * @return
     */
    @GetMapping("/info")
    public NServerInfo getInfo(@RequestParam("ip") String ip) {

        NServerInfo info = new NServerInfo();

        String[] hostAndPort = ip.split(":");

        if (hostAndPort.length == 2) {
            info.setHost(hostAndPort[0]);
            info.setPort(Integer.valueOf(hostAndPort[1]));
        }


        try {
            info.setTotalConnection(configs.getCountMap().get(ip).get());
        } catch (Exception e) {
            throw new ChatHttpRequestException("获取数量失败", e);
        }

        return info;
    }
}
