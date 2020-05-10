package com.misc.server.spi;

import com.misc.core.exception.HandlerException;
import com.misc.core.model.MiscPack;
import com.misc.core.model.URL;
import com.misc.core.model.netty.Request;
import com.misc.core.spi.SPIUtil;
import com.misc.core.util.NetUtils;
import com.misc.server.handler.MiscServerContext;
import com.misc.server.handler.ServerReadMiscEventHandler;
import com.misc.server.spi.defaulthandler.DefaultHandlerChainBuilder;
import com.misc.server.spi.filter.DefaultFilter;
import com.misc.server.spi.filter.Filter;
import com.misc.server.spi.handler.HandlerChainBuilder;
import com.misc.server.spi.handler.RequestHandlerProcess;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

/**
 * 服务器读处理
 * {@link ServerReadMiscEventHandler}
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
    private final RequestHandlerProcess process;

    /**
     * context
     */
    private final MiscServerContext context;

    private final String host;

    private final int port;

    private final short version;

    /**
     * 构造方法 , SPI 加载
     */
    public HandlerReceivePackage(MiscServerContext context) {
        this.context = context;
        this.host = NetUtils.filterLocalHost(context.getHost());
        this.port = context.getPort();
        this.version = context.getVersion();
        this.filter = SPIUtil.loadFirstInstanceOrDefault(Filter.class, DefaultFilter.class);
        HandlerChainBuilder builder = SPIUtil.loadFirstInstanceOrDefault(HandlerChainBuilder.class, DefaultHandlerChainBuilder.class);
        this.process = Objects.requireNonNull(builder).build();
    }

    /**
     * 处理器  : 过滤器 和 执行器
     *
     * @param pack 数据包
     * @throws HandlerException 可能处理异常, 抛出
     */
    public void handlerNPack(MiscPack pack) throws HandlerException {
        ChannelHandlerContext channelContext = null;
        Request request = null;
        try {
            request = buildRequest(pack);
            // 被拦截了直接不处理.
            if (this.filter.doFilter(request)) {
                return;
            }
            // 获取他关联的 context, 这个SocketAddress的hashcode是一个重载的方法.
            channelContext = this.context.getContext(pack.getAddress());
            // 从第一个去处理 , 处理空异常
            Objects.requireNonNull(process.getFirst()).handler(request, channelContext);
        } finally {
            if (request != null) {
                // 清空我们的引用对象.释放内存.
                request.release();
            }
        }
    }


    private Request buildRequest(MiscPack pack) {
        URL url = URL.valueOfByDecode(pack.getRouter());
        byte[] body = pack.getBody();
        if (body == null || body.length == 0) {
            return new Request(url, null, pack.getTimestamp(), this.host, this.port, this.version);
        }
        return new Request(url, pack.getBody(), pack.getTimestamp(), this.host, this.port, this.version);
    }


}
