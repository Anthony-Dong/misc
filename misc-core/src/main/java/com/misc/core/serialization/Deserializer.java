package com.misc.core.serialization;

import com.misc.core.exception.SerializationException;

/**
 * 反序列化
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface Deserializer<T> {

    T deserialize(byte[] arr) throws SerializationException;
}
