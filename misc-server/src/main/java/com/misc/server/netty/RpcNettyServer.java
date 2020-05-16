package com.misc.server.netty;


import com.misc.core.model.rpc.RpcRequest;
import com.misc.core.model.rpc.RpcResponse;
import com.misc.core.netty.NettyServer;



/**
 * 服务器端
 */
public class RpcNettyServer<ProtoInBound, ProtoOutBound> extends NettyServer.Builder<RpcRequest, RpcResponse,ProtoInBound, ProtoOutBound> {


}
