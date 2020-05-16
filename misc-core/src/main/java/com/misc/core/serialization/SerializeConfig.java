package com.misc.core.serialization;


import com.misc.core.exception.SerializationException;
import com.misc.core.util.LRUCache;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class SerializeConfig {
    // serialClassName
    private static final LRUCache<String, Serializer> SERIALIZER_CACHE = new LRUCache<>();

    private static final LRUCache<String, Deserializer> DESERIALIZER_CACHE = new LRUCache<>();

    private static final JsonObjectSerialization DEFAULT_SERIALIZATION = new JsonObjectSerialization();

    /**
     * 获取反序列化器
     */
    public static Deserializer getDeserializer(String deserializerClassName) throws SerializationException {
        Deserializer deserializer = DESERIALIZER_CACHE.get(deserializerClassName);
        if (deserializer == null) {
            try {
                Class<?> clazz = Class.forName(deserializerClassName);
                deserializer = (Deserializer) clazz.newInstance();
                DESERIALIZER_CACHE.put(deserializerClassName, deserializer);
                return DESERIALIZER_CACHE.get(deserializerClassName);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new SerializationException(e);
            }
        } else {
            return deserializer;
        }
    }


    /**
     * 获取序列化器
     */
    public  static Serializer getSerializer(String serializerClassName) throws SerializationException {
        Serializer serializer = SERIALIZER_CACHE.get(serializerClassName);
        if (serializer == null) {
            try {
                Class<?> clazz = Class.forName(serializerClassName);
                serializer = (Serializer) clazz.newInstance();
                SERIALIZER_CACHE.put(serializerClassName, serializer);
                return SERIALIZER_CACHE.get(serializerClassName);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new SerializationException(e);
            }
        } else {
            return serializer;
        }
    }

}
