package com.chat.server.spi;

import com.chat.core.model.NPack;

/**
 * 默认实现
 *
 * @date:2019/12/26 19:56
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class DefaultFilter implements Filter {

    @Override
    public boolean doFilter(NPack pack) {
        return false;
    }
}
