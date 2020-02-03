package com.chat.server;

import com.chat.core.annotation.Primary;
import com.chat.core.exception.HandlerException;
import com.chat.core.model.NPack;
import com.chat.core.spi.SPIUtil;
import com.chat.server.handler.ServerReadChatEventHandler;
import com.chat.server.spi.Filter;
import com.chat.server.spi.HandlerReceivePackage;
import com.chat.server.spi.SaveReceivePackage;
import org.junit.Test;

import java.util.*;

/**
 * TODO
 *
 * @date:2019/12/25 10:32
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class TestSPI {

    public static void main(String[] args) {
        ServiceLoader<HandlerReceivePackage> services = ServiceLoader.load(HandlerReceivePackage.class, ServerReadChatEventHandler.class.getClassLoader());
        Iterator<HandlerReceivePackage> iterator = services.iterator();
        HandlerReceivePackage handlerReceivePackage = null;

        List<HandlerReceivePackage> defaultSet = new ArrayList<>();

        TreeMap<Integer, HandlerReceivePackage> primarySet = new TreeMap<>();

        while (iterator.hasNext()) {
            HandlerReceivePackage next = iterator.next();
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


        System.out.println(handlerReceivePackage);
    }


    @Test
    public void test() throws HandlerException {
        SaveReceivePackage saver = SPIUtil.loadClass(SaveReceivePackage.class, Thread.currentThread().getContextClassLoader());
        assert saver != null;
//        saver.doSave(NPack.buildWithStringBody("a", "b", "c"));
    }
}
