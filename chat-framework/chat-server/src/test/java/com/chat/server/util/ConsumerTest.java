package com.chat.server.util;

import redis.clients.jedis.Jedis;

public class ConsumerTest {
 
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost", 6379);
		MessageHandler handler = new MessageHandler();
		jedis.subscribe(handler, "channel1");
	}
}
