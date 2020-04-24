package com.chat.client.spi;

import com.chat.core.annotation.SPI;
import io.netty.channel.ChannelHandlerContext;

/**
 * @date:2019/12/25 9:54
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@SPI
public interface HandlerSenderPackage {

    void senderPack(ChannelHandlerContext context);

}
