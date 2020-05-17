package com.misc.core.serialization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.misc.core.exception.SerializationException;

import java.lang.reflect.Type;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class JsonObjectArraySerialization implements Deserializer<Object[], Class<?>[]>, Serializer<Object[]> {

    /**
     * 序列化类型
     */
    private static final Type TYPE = new TypeReference<Object[]>() {

    }.getType();

    @Override
    public Object[] deserialize(byte[] arr, Class<?>[] types) throws SerializationException {
        if (arr == null || arr.length == 0) return null;
        return JSONArray.parseArray(new String(arr), types).toArray();
    }

    @Override
    public byte[] serialize(Object[] obj) {
        if (obj == null || obj.length == 0) {
            return null;
        }
        return JSON.toJSONBytes(obj);
    }
}
