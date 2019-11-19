package com.chat.conf.model;

import com.chat.conf.spring.ChatConfigurationProperties;

/**
 *  配置中心的部分常量
 *
 * @date:2019/11/12 17:57
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ConfConstant {

    /**
     * 没用的字段
     */
    public static final String HTTP_ADDRESS = "HTTP_ADDRESS";

    /**
     * {@link ChatConfigurationProperties hashMap} 中保存的是这个
     */
    public static final String CHAT_SERVER_IP = "CHAT_SERVER_IP";


    /**
     *  定时器的工厂名称
     */
    public static final String CONF_SCHEDULE_EXECUTOR = "Chat-Conf-Executor";

}
