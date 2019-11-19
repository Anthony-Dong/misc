package com.chat.spring.model;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Component
//@PropertySource({"classpath:server.properties"})
@ConfigurationProperties(prefix = "chat.server")
public class ServerConfigs implements InitializingBean {

	/**
	 * key值是通过   host:id  生成的
	 */
	private HashMap<String, AtomicInteger> countMap = new HashMap<>();

	/**
	 * 服务器ip
	 */
	private String socketHost="localhost";
	/**
	 * 服务器端口
	 */
	private List<Integer> socketPort;

	/**
	 * HTTP 服务 IP ,默认是 HTTP
	 */
	private Boolean serverIsHttp=true;
	private String httpPrefix = "";
	private static final String HTTP = "http://";
	private static final String HTTPS = "http://";
	private String httpHost = "localhost";

	@Value("${server.port}")
	private int httpPort=8080;


	/**
	 * REDIS
	 */
	private String redisHost="localhost";
	private Integer redisPort=6379;
	private Integer poolMax=10;

	/**
	 * 服务器的客户端总连接
	 */
	private AtomicInteger totalConnection;

	/**
	 * zk 信息
	 */
	private String zookeeperIp="localhost:2181";
	private int zookeeperTimeout;
	private String zookeeperPath = "/chat";


	@Override
	public void afterPropertiesSet() throws Exception {

		if (serverIsHttp) {
			httpPrefix = HTTP;
		} else {
			httpPrefix = HTTPS;
		}

		// 根据   host:port 为 key  和 value 是一个 计数器
		socketPort.forEach(e->{
			countMap.put(socketHost+":"+e, new AtomicInteger(0));
		});
	}
}