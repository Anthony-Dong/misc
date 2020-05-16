package com.misc.core.proto.misc;

import com.misc.core.model.MiscPack;
import com.misc.core.model.MiscRequest;
import com.misc.core.model.MiscResponse;
import com.misc.core.model.URL;
import com.misc.core.model.rpc.RpcRequest;
import com.misc.core.util.SystemUtil;

import java.util.HashMap;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscProtoHandler  {

    public Object handlerRequest(MiscRequest request) {
        MiscPack miscPack = new MiscPack();
        RpcRequest rpcRequest = (RpcRequest) request;
        miscPack.setRouter(rpcRequest.exportUrl());
        miscPack.setBody(rpcRequest.exprotBody());
        miscPack.setTimestamp(System.currentTimeMillis());
        return miscPack;
    }

    public Object handlerResponse(MiscResponse object) {

        return null;
    }
}
