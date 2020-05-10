package com.misc.server.spi.filter;

import com.misc.core.annotation.SPI;
import com.misc.core.exception.HandlerException;
import com.misc.core.model.netty.Request;

/**
 * true 过滤
 * <p>
 * false 不过滤
 * <p>
 * 其实是还想传入 ThreadPool的. 算了. 我们的目的主要是过滤铭感信息
 *
 * @date:2019/12/26 19:55
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@SPI
public interface Filter {
    boolean doFilter(Request request) throws HandlerException;
}
