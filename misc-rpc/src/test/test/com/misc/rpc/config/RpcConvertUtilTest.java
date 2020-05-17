package com.misc.rpc.config;

import com.misc.core.serialization.Deserializer;
import com.misc.core.serialization.JsonObjectArraySerialization;
import com.misc.core.serialization.SerializationFactory;
import com.misc.core.serialization.Serializer;
import com.misc.core.test.User;
import lombok.*;
import org.junit.Test;


public class RpcConvertUtilTest {

    @SuppressWarnings("all")
    @Test
    public void convertMiscPackToRpcRequest() throws Exception {
        JsonObjectArraySerialization serialization = new JsonObjectArraySerialization();

        SerializationFactory serializationFactory = new SerializationFactory();
        Serializer<Object[]> serializerFromCache = serializationFactory.getSerializerFromCache(serialization.getClass().getName());
        Object[] objects = {
                "1", 1234
        };
        byte[] serialize = serializerFromCache.serialize(objects);

        Deserializer<Object[], Class<?>[]> deserializerFromCache = serializationFactory.getDeserializerFromCache(serialization.getClass().getName());

        Object[] deserialize = deserializerFromCache.deserialize(serialize, new Class<?>[]{
                String.class,
                Long.class
        });
        for (Object o : deserialize) {
            System.out.println(o.getClass());
        }
    }

    @Test
    public void convertRpcRequestToMiscPack() {
    }


}