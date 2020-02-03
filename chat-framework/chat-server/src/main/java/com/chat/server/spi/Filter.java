package com.chat.server.spi;

import com.chat.core.annotation.SPI;
import com.chat.core.exception.HandlerException;
import com.chat.core.model.NPack;

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
    boolean doFilter(NPack pack) throws HandlerException;
}
