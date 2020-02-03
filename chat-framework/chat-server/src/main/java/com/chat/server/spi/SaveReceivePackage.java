package com.chat.server.spi;

import com.chat.core.annotation.SPI;
import com.chat.core.exception.HandlerException;
import com.chat.core.model.NPack;
import io.netty.channel.ChannelHandlerContext;

/**
 * 保存 接收到的package
 */
@SPI
public interface SaveReceivePackage {

    /**
     * @param pack NPack 数据包
     * @param context 当前连接的上下文. 可以用来发送消息之类的.
     * @throws HandlerException 异常
     */
    void doSave(NPack pack, ChannelHandlerContext context) throws HandlerException;
}
