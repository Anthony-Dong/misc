package com.chat.spring.env;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @date:2019/12/26 16:58
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = NettyServerProperties.PREFIX)
public class NettyServerProperties {

    static final String PREFIX = "chat";

    private int port;

    private String contextName;


}
