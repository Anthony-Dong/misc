package com.chat.server.spi;

import com.chat.core.exception.HandlerException;
import com.chat.core.model.NPack;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @date:2020/1/7 20:54
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class DefaultSaveReceivePackage implements SaveReceivePackage {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSaveReceivePackage.class);

    /**
     * 保存 数据包的唯一拓展接口
     *
     * @param pack NPack 数据包
     * @throws HandlerException 异常
     */
    @Override
    public void doSave(NPack pack, ChannelHandlerContext context) throws HandlerException {
        LOGGER.info("[服务器] Receive Pack : {}.", pack);
        for (int x = 1; x < 10; x++) {
            context.writeAndFlush(NPack.buildWithJsonBody("服务器", "OK", "NULL"));
        }
    }
}
