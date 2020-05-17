package com.misc.core.serialization;

import com.misc.core.exception.SerializationException;

/**
 * 序列化
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface Serializer<V> {

    /**
     * v 是需要序列化的对象
     */
    byte[] serialize(V obj) throws SerializationException;
}
