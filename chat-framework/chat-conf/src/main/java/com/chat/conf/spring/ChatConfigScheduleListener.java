package com.chat.conf.spring;


import com.alibaba.fastjson.TypeReference;
import com.chat.core.model.NServerInfo;
import com.chat.core.util.HttpUtil;
import com.chat.conf.model.ConfConstant;
import com.chat.core.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.*;

/**
 * @date:2019/11/12 19:01
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Slf4j
@Component
public class ChatConfigScheduleListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ChatConfigurationProperties info;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {


        log.info("[配置中心] 启动定时刷新器成功 ...");

        // 这是一个线程池 , 任务线程池
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10, new ChatThreadFactory(ConfConstant.CONF_SCHEDULE_EXECUTOR));


        // 线程池任务
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                // 目的主要是为了去重 ,需要重写 NServerInfo 的hashcode方法
                HashSet<NServerInfo> serverInfoHashSet = info.getChatServerInfos();

                // 每次都要去拉去一下 , 不然这个线程不回去刷新对象的 .
                CopyOnWriteArrayList<String> nettyServerIp = info.getNettyServerInfo().get(ConfConstant.CHAT_SERVER_IP);

                // 遍历所有的远程地址
                for (String e : nettyServerIp) {
                    String json = null;
                    try {
                        json = HttpUtil.doGet(e);
                    } catch (IOException e1) {
                        // 只要是错误 我们就将 set表清空 ,其实我这种做法不好 ,可以将他做成一个 hashMap ,可以更好的表现
                        serverInfoHashSet.clear();
                        // TODO: 2019/11/13  记录错误日志
                        log.info("[配置中心] 远程拉去信息失败 远程地址 : {} ", e);

                        // 如果我们抛出异常 , 用throw 这个线程会停止 ,不会帮我们去刷新
                        continue;
                    }

                    // 序列化成 对象 , 存入到 队列里 ,
                    NServerInfo nServerInfo = JsonUtil.parseObject(json, new TypeReference<NServerInfo>() {
                    });

                    // 存入set中是为了去重,这里不用担心多线程问题 , 如果需要加一个 sync
                    serverInfoHashSet.add(nServerInfo);
                    log.info("[配置中心] 刷新一条聊天服务器信息成功 : {} ",nServerInfo);
                }
            }
        }, 0, info.getPullScheduleInterval(), TimeUnit.SECONDS);

    }
}
