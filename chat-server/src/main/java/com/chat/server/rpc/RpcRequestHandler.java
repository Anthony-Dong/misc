package com.chat.server.rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chat.core.exception.HandlerException;
import com.chat.core.exception.ProxyException;
import com.chat.core.model.NPack;
import com.chat.core.model.URL;
import com.chat.core.model.netty.Arg;
import com.chat.core.model.netty.ArgsUtil;
import com.chat.core.model.netty.Request;
import com.chat.server.spi.handler.AbstractRequestHandler;
import io.netty.channel.ChannelHandlerContext;

import static com.chat.core.model.UrlConstants.METHOD_KEY;
import static com.chat.core.model.UrlConstants.RPC_PROTOCOL;
import static com.chat.core.model.UrlConstants.ID_KEY;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;

/**
 * @date:2020/2/17 14:43
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcRequestHandler extends AbstractRequestHandler {
    private final RpcMap rpcMap;


    public RpcRequestHandler(RpcMap builder) {
        this.rpcMap = builder;
    }

    @Override
    public void handler(Request request, ChannelHandlerContext context) throws HandlerException {
        if (request.getProtocol().equals(RPC_PROTOCOL)) {
            if (System.currentTimeMillis() - request.getTimestamp() > request.getTimeout()) {
                fireHandler(request, context);
            }
            Object result = handlerInvoke(request);
            String router = URL.encode(new URL(request.getProtocol(), request.getHost(), request.getPort(), Collections.singletonMap(ID_KEY, request.getId())).toString());
            NPack pack = null;
            if (result == null) {
                pack = new NPack(router);
            } else {
                byte[] body = JSON.toJSONString(result).getBytes();
                pack = new NPack(router, body);
            }
            context.writeAndFlush(pack);
        } else {
            fireHandler(request, context);
        }
    }

    /**
     * 处理调用RPC调用
     */
    private Object handlerInvoke(Request request) throws ProxyException {
        URL url = request.getUrl();
        // 接口名称
        String path = url.getPath();

        String methodName = url.getParameter(METHOD_KEY);

        String mm = path + "." + methodName;
        // 获取method
        Method method = rpcMap.getMethodMap().get(mm);

        if (method == null) {
            throw new IllegalArgumentException(String.format("调用%s方法错误\n", mm));
        }

        Object obj = rpcMap.getObjectMap().get(path);
        if (obj == null) {
            throw new IllegalArgumentException(String.format("调用%s方法错误\n", mm));
        }

        try {
            byte[] body = request.getBody();
            Object[] objects = null;
            // 不需要就不转换了
            if (body != null && body.length != 0) {
                objects = ArgsUtil.convert(body, method);
            }
            return method.invoke(obj, objects);
        } catch (Throwable e) {
            throw new ProxyException(String.format("调用%s方法错误,错误信息:%s", mm, e.getMessage()));
        }
    }
}
