package com.misc.core.serialization;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class JsonObjectSerialization implements Deserializer<Object>, Serializer<Object> {

    @Override
    public Object deserialize(byte[] arr) {
        return null;
    }

    @Override
    public byte[] serialize(Object o) {
        return new byte[0];
    }
}
