package com.chat.core.spi;

import com.chat.core.annotation.MayEmpty;
import com.chat.core.annotation.Primary;
import com.chat.core.annotation.SPI;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI 拓展机制
 *
 * @date:2019/12/25 10:31
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class SPIUtil {

    /**
     * {@link Primary} 根据其优先级判断, 选择最高优先级
     * <p>
     * 如果你不实现就是系统默认实现的
     *
     * @param target 目标类
     * @param loader 类加载器
     * @param <T>    返回目标对象
     * @return 返回目标对象(优先级最高的)
     */
    @MayEmpty
    public static <T> T loadClass(Class<T> target, ClassLoader loader) {

        SPI annotation = target.getAnnotation(SPI.class);
        if (annotation == null) {
            return null;
        }

        ServiceLoader<T> services = ServiceLoader.load(target, loader);
        Iterator<T> iterator = services.iterator();
        T handlerReceivePackage = null;

        // 默认我的实现
        List<T> defaultSet = new ArrayList<>();

        TreeMap<Integer, T> primarySet = new TreeMap<>();

        while (iterator.hasNext()) {
            T next = iterator.next();
            Primary primary = next.getClass().getAnnotation(Primary.class);
            if (primary == null) {
                defaultSet.add(next);
            } else {
                primarySet.put(primary.order(), next);
            }
        }

        if (primarySet.size() == 0) {
            handlerReceivePackage = defaultSet.get(0);
        } else {
            Integer integer = primarySet.lastKey();
            handlerReceivePackage = primarySet.get(integer);
        }

        // 返回
        return handlerReceivePackage;
    }

    /**
     * 获取默认值
     */
    public static <T> T loadFirstInstanceOrDefault(Class<T> clazz, Class<? extends T> defaultClass) {
        try {
            // Not thread-safe, as it's expected to be resolved in a thread-safe context.
            ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz, Thread.currentThread().getContextClassLoader());

            for (T instance : serviceLoader) {
                if (instance != defaultClass) {
                    return instance;
                }
            }
            return defaultClass.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
}
