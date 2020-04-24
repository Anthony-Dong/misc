package com.chat.server.spi.handler;

import com.chat.core.annotation.SPI;

/**
 * 构建 RequestHandlerProcess 可以处理执行链 , 用户可以通过SPI注入这个
 *
 * @date:2020/2/17 14:30
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@SPI
public interface HandlerChainBuilder {

    /**
     * 构建一个 RequestHandlerProcess
     */
    RequestHandlerProcess build();
}
