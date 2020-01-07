package com.chat.spring.env;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @date:2019/12/26 16:58
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@ConfigurationProperties(prefix = ChatServerProperties.PREFIX, ignoreInvalidFields = true, ignoreUnknownFields = true)
public class ChatServerProperties {

    static final String PREFIX = "chat";

    private int port;

    private short version;

//    private Jedis redis = new Jedis();

    private String contextName;

    public static class Jedis {
        String host = "localhost";
        int port = 6379;
        int maxIdle = 10;
        int minIdle = 5;
        int maxTotal = 10;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxTotal() {
            return maxTotal;
        }

        public void setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
        }
    }


    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }
}
