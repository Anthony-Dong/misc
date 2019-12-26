package com.chat.spring;

import com.chat.core.model.NPack;
import com.chat.core.spi.SPIUtil;
import com.chat.server.spi.Filter;
import org.junit.Test;

public class ChatServerSpringbootStartApplicationTests {

	@Test
	public void contextLoads() {

		Filter filter = SPIUtil.loadClass(Filter.class, ChatServerSpringbootStartApplicationTests.class.getClassLoader());


		ClassLoader classLoader = ChatServerSpringbootStartApplicationTests.class.getClassLoader();

		System.out.println("classLoader = " + classLoader);

		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

		System.out.println(systemClassLoader);


		System.out.println(filter);

		assert filter != null;
		boolean b = filter.doFilter(NPack.buildWithStringBody("a", "a", "a"));

		System.out.println(b);
	}

}
