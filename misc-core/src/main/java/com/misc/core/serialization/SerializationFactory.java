package com.misc.core.serialization;

import com.misc.core.util.LRUCache;

/**
 * todo
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class SerializationFactory {
    public static final JsonObjectArraySerialization DEFAULT_SERIALIZATION = new JsonObjectArraySerialization();

    public static final JsonObjectSerialization DEFAULT_OBJECT_SERIALIZATION = new JsonObjectSerialization();

    //
    private static final LRUCache<String, Serializer> serializerCache = new LRUCache<>(128);

    //
    private static final LRUCache<String, Deserializer> deserializerCache = new LRUCache<>(128);


    /**
     * 从 cache 中获取
     */
    public Deserializer getDeserializerFromCache(String deserializerClassName) throws Exception {
        Deserializer deserializer = deserializerCache.get(deserializerClassName);
        if (deserializer == null) {
            Deserializer target = (Deserializer) Class.forName(deserializerClassName).newInstance();
            deserializerCache.put(deserializerClassName, target);
            return deserializerCache.get(deserializerClassName);
        }
        return deserializer;
    }


    /**
     * 从 cache中获取
     */
    public Serializer getSerializerFromCache(String serializerClassName) throws Exception {
        Serializer serializer = serializerCache.get(serializerClassName);
        if (serializer == null) {
            Serializer target = (Serializer) Class.forName(serializerClassName).newInstance();
            serializerCache.put(serializerClassName, target);
            return serializerCache.get(serializerClassName);
        }
        return serializer;
    }

}
