package com.misc.core.model.rpc;

import com.misc.core.model.MiscResponse;
import com.misc.core.serialization.Serializer;


/**
 * 响应结果
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RpcResponse extends MiscResponse {

    private static final long serialVersionUID = -4577662869748561136L;

    private transient Serializer<Object> serializable;

    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Serializer<Object> getSerializable() {
        return serializable;
    }

    public void setSerializable(Serializer<Object> serializable) {
        this.serializable = serializable;
    }

    @Override
    public void release() {

    }
}
