package com.chat.core.util;

import org.junit.Test;
import sun.net.util.URLUtil;

import java.util.Properties;

import static org.junit.Assert.*;

public class RouterUtilTest {

    @Test
    public void getRouter() throws Exception {
        String router = RouterUtil.getRouterByString("null", "xiaoli", "xiaowang");

        System.out.println("router = " + router);


        Properties properties = RouterUtil.convertRouter(router);

        System.out.println("properties.getProperty(RouterUtil.SENDER) = " + properties.getProperty(RouterUtil.SENDER));

    }

    @Test
    public void convertRouter(){
        String router = RouterUtil.getRouterByString("null", "xiaoli", "xiaowang");

    }


    @Test
    public void testUrl(){






    }
}