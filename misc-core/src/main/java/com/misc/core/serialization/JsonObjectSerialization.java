package com.misc.core.serialization;

import com.alibaba.fastjson.JSON;
import com.misc.core.exception.SerializationException;

/**
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class JsonObjectSerialization implements Deserializer<Object, Class<?>>, Serializer<Object> {

    @Override
    public byte[] serialize(Object obj) throws SerializationException {
        if (obj == null) return null;
        return JSON.toJSONBytes(obj);
    }

    @Override
    public Object deserialize(byte[] arr, Class<?> type) throws SerializationException {
        if (arr == null || arr.length == 0) return null;
        Object result = null;
        try {
            result = JSON.parseObject(arr, type);
        } catch (Exception e) {
            result = JSON.parse(arr);
        }
        return result;
    }
}
