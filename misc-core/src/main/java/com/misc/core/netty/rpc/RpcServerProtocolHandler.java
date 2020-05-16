package com.misc.core.netty.rpc;

import com.misc.core.exception.ProtocolException;
import com.misc.core.model.MiscPack;
import com.misc.core.model.URL;
import com.misc.core.model.rpc.RpcRequest;
import com.misc.core.model.rpc.RpcResponse;
import com.misc.core.netty.ProtocolHandler;
import com.misc.core.proto.ProtocolType;
import com.misc.core.serialization.Deserializer;
import com.misc.core.serialization.SerializeConfig;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.List;

/**
 * rpc 协议处理器处理器
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcServerProtocolHandler extends ProtocolHandler {

    private final ProtocolType protocolType;

    public RpcServerProtocolHandler(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    /**
     * 根据协议转换各种类型
     */
    @Override
    protected Object decode(Object msg) {
        switch (protocolType) {
            case MISC_PROTO:
                return miscDecodeRpcResponse((MiscPack) msg);
            case HTTP_PROTO:
                return httpDecodeRpcResponse((FullHttpRequest) msg);
            default:
                throw new ProtocolException("协议类型不支持");
        }
    }


    @Override
    protected Object  encode(Object msg) {
        switch (protocolType) {
            case MISC_PROTO:
                return miscEncodeRpcResponse((RpcResponse) msg);
            case HTTP_PROTO:
                return httpEncodeRpcResponse((RpcResponse) msg);
            default:
                throw new ProtocolException("协议类型不支持");
        }
    }

    /**
     * 将miscpack 转换成 rpcrequest
     *
     * @param pack
     * @return
     * @throws RuntimeException
     */
    @SuppressWarnings("all")
    private static RpcRequest miscDecodeRpcResponse(MiscPack pack) throws ProtocolException {
        RpcRequest request = new RpcRequest();
        URL url = URL.valueOf(pack.getRouter());
        //
        request.setUrl(url);
        request.setTimeout(url.getTimeout());
        request.setInterfaceName(url.getServiceInterface());
        String deserializerClassName = url.getDeserializerClassName();
        Deserializer deserializer = SerializeConfig.getDeserializer(deserializerClassName);
        List<Object> deserialize = (List<Object>) deserializer.deserialize(pack.getBody());
        request.setParams(deserialize.toArray());
        return request;
    }


    /**
     * 将 rpc response 转换成 misc
     */
    private static MiscPack miscEncodeRpcResponse(RpcResponse response) throws ProtocolException {
        try {
            Object result = response.getResult();
            URL url = response.getUrl();
            long timeStamp = response.getTimeStamp();
            MiscPack miscPack = new MiscPack();
            miscPack.setBody(response.getSerializable().serialize(result));
            miscPack.setTimestamp(timeStamp);
            miscPack.setRouter(url.toString());
            return miscPack;
        } finally {
            response.release();
        }
    }

    /**
     * FullHttpRequest 转换成 RpcRequest 交给下一级的处理器
     */
    private static RpcRequest httpDecodeRpcResponse(FullHttpRequest request) throws ProtocolException {
        return null;
    }

    /**
     * RpcResponse 转换成 FullHttpResponse 交给上一级的处理器
     */
    private static FullHttpResponse httpEncodeRpcResponse(RpcResponse request) throws ProtocolException {
        return null;
    }

}
