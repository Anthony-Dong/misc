package com.chat.client.future;

import com.alibaba.fastjson.JSON;
import com.chat.core.exception.TimeOutException;
import com.chat.core.model.NPack;
import com.chat.core.model.netty.Response;

/**
 * @date:2020/3/3 14:02
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ResponseFuture extends NpackFuture {

    private final Class<?> returnType;

    public ResponseFuture(int id, NPack pack, Class<?> returnType) {
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
