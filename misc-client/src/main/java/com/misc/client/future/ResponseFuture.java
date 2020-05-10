package com.misc.client.future;

import com.alibaba.fastjson.JSON;
import com.misc.core.exception.TimeOutException;
import com.misc.core.model.MiscPack;
import com.misc.core.model.netty.Response;

/**
 * @date:2020/3/3 14:02
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ResponseFuture extends MiscFuture {

    private final Class<?> returnType;

    public ResponseFuture(int id, MiscPack pack, Class<?> returnType) {
        super(id, pack);
        this.returnType = returnType;
    }


    public Object getResult(long timeout) throws TimeOutException {
        Response response = get(timeout);
        byte[] result = response.getResult();
        if (result == null || result.length == 0) {
            return null;
        }
        return JSON.parseObject(result, returnType);
    }
}
