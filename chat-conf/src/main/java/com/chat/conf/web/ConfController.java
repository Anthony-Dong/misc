package com.chat.conf.web;

import com.chat.conf.exception.ChatHttpRequestException;
import com.chat.conf.spring.ChatConfigurationProperties;
import com.chat.conf.model.NServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


/**
 * conf 的 web 接口
 *
 * @date:2019/11/12 17:41
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@RequestMapping("/chat/conf")
@RestController
public class ConfController {

    @Autowired
    private ChatConfigurationProperties info;


    /**
     * 获取连接数最小的 server , 可以优化的 ,top-k 算法之类的
     * 还比如 每一个server端 可能有多个 netty-server ,尽量不在一个server
     *
     * @return
     */
    @GetMapping("/reg/{num}")
    public List<NServerInfo> reg(@PathVariable("num") Integer num) {

        HashSet<NServerInfo> infos = info.getChatServerInfos();

        if (infos.size() < num) {
            throw new ChatHttpRequestException("数量不足");
        }
        return infos.stream().sorted(Comparator.comparingInt(NServerInfo::getTotalConnection)).limit(num).collect(Collectors.toList());
    }
}
