package com.chat.spring.model;

import com.chat.client.util.RedisPool;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;

/**
 * chat.client.configServerPort=8080
 * chat.client.configServerHost=localhost
 * chat.client.configServerIsHttp=true
 * chat.client.clientAmount=1
 * chat.client.redisHost=localhost
 * chat.client.redisPort=6379
 * chat.client.redisPoolSize=10
 *
 * @date:2019/11/13 12:34
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Data
@Component
@ConfigurationProperties(prefix = "chat.client")
public class ChatClientProperties implements InitializingBean {

    /**
     * 配置中心信息
     */
    private Integer configServerPort = 9999;

    private String configServerHost = "localhost";

    private boolean configServerIsHttp = true;

    /**
     * 客户端要创建几个
     */
    private Integer clientAmount = 1;


    /**
     * redis 信息
     */
    private String redisHost = "localhost";
    private Integer redisPort = 6379;
    private Integer redisPoolSize = 10;

    private RedisPool redisPool;

    /**
     * 这些不需要手动配置
     */
    private String url = "/chat/conf/reg";

    private static final String HTTP_PREFIX = "http://";

    private static final String HTTPS_PREFIX = "https://";

    private String ConfigHttpUri = "http://localhost:8080/chat/conf/reg/1";


    @Override
    public void afterPropertiesSet() throws Exception {

        this.redisPool = new RedisPool(this.redisPoolSize, new HostAndPort(this.redisHost, this.redisPort));

        if (configServerIsHttp) {
            //例如   http://localhost: 8080/chat/conf/reg/2
            ConfigHttpUri = HTTP_PREFIX + configServerHost + ":" + configServerPort + url + "/" + clientAmount;

        } else {

            // 例如  https://localhost: 8080/chat/conf/reg/2
            ConfigHttpUri = HTTPS_PREFIX + configServerHost + ":" + configServerPort + url + "/" + clientAmount;
        }
    }
}
