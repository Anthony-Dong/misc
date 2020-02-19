package com.chat.server.spi.defaulthandler;

import com.chat.core.inter.EchoService;
import com.chat.server.rpc.RpcMap;
import com.chat.server.rpc.RpcRequestHandler;
import com.chat.server.spi.handler.HandlerChainBuilder;
import com.chat.server.spi.handler.RequestHandlerProcess;
import com.chat.server.spi.test.TestEchoService;


/**
 * 默认的构建器 {@link HandlerChainBuilder}
 *
 * @date:2020/2/17 14:31
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class DefaultHandlerChainBuilder implements HandlerChainBuilder {

    /**
     * 链式处理器
     */
    @Override
    public RequestHandlerProcess build() {
        RequestHandlerProcess process = new RequestHandlerProcess();
        process.addLast(new RecordRequestHandler());
//        process.addLast(new LogRequestHandler());
        process.addLast(new MessageRequestHandler());
//        process.addLast(new FileRequestHandler());
        process.addLast(new RpcRequestHandler(RpcMapBuilder.map));
        process.addLast(new HeartRequestHandler());
        return process;
    }
}
