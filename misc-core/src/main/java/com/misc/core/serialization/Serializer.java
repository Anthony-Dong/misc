package com.misc.core.serialization;

import com.misc.core.exception.SerializationException;

/**
 * 序列化
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface Serializer<T> {

    byte[] serialize(T t) throws SerializationException;
}
