package com.chat.conf.util;


import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 序列化与反序列java的
 *
 * @date:2019/9/27 13:16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class SerializationUtil {



    public static Object getObject(byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        try(ByteInputStream byteInputStream = new ByteInputStream(bytes, 0, bytes.length);
            ObjectInputStream objectInputStream=new ObjectInputStream(byteInputStream)) {
            return objectInputStream.readObject();
        } catch (Exception e) {
            throw  new RuntimeException("SerializationUtil 反序列化异常");
        }
    }

    /**
     *
     * @param bean
     * @return
     */
    public static byte[] getByte(Object bean){
        if (null == bean) {
            return null;
        }
        try (ByteOutputStream bos = new ByteOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(bean);
            return bos.getBytes();
        } catch (Exception e) {
            throw  new RuntimeException("SerializationUtil 序列化异常");
        }
    }
}
