package com.misc.core.serialization;

import com.misc.core.exception.SerializationException;

/**
 * 反序列化
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface Deserializer<R, T> {

    /**
     * t 是用来告诉凡序列化成什么类型的
     * r 是结果
     */
    R deserialize(byte[] arr, T type) throws SerializationException;
}
