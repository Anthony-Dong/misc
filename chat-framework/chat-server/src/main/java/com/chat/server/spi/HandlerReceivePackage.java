package com.chat.server.spi;

import com.chat.core.exception.HandlerException;
import com.chat.core.model.NPack;
import com.chat.core.spi.SPIUtil;
import com.chat.server.handler.ChatServerContext;
import com.chat.server.handler.ServerReadChatEventHandler;
import io.netty.channel.ChannelHandlerContext;


/**
 * 服务器读处理
 * {@link ServerReadChatEventHandler}
 *
 * @date:2019/12/25 8:43
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class HandlerReceivePackage {

    /**
     * 过滤器
     */
    private final Filter filter;

    /**
     * 保存数据包
     */
    private final SaveReceivePackage saver;

    private final ChatServerContext context;

    /**
     * 构造方法 , SPI 加载
     */
    public HandlerReceivePackage(ChatServerContext context) {
        this.context = context;
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        this.filter = SPIUtil.loadClass(Filter.class, classLoader);
        this.saver = SPIUtil.loadClass(SaveReceivePackage.class, classLoader);
    }

    /**
     * 处理器  : 过滤器 和 执行器
     *
     * @param pack 数据包
     * @throws HandlerException 可能处理异常, 抛出
     */
    public void handlerNPack(NPack pack) throws HandlerException {
        try {
            if (this.filter.doFilter(pack)) {
                return;
            }
            ChannelHandlerContext context = this.context.getContext(pack.getAddress());
            saver.doSave(pack, context);
        } finally {
            pack.release();
        }
    }
}
