package com.chat.server.spi;

import com.chat.core.exception.HandlerException;
import com.chat.server.spi.handler.AbstractRequestHandler;
import com.chat.server.spi.handler.RequestHandlerProcess;
import org.junit.Test;

public class RequestHandlerBuilderTest {

    @Test
    public void test() throws HandlerException {
        RequestHandlerProcess builder = new RequestHandlerProcess();

//        AbstractRequestHandler handlerPackage1 = new AbstractRequestHandler();

//        AbstractRequestHandler handlerPackage2 = new AbstractRequestHandler();

//        AbstractRequestHandler handlerPackage3 = new AbstractRequestHandler();
//
//        builder.addFirst(handlerPackage1);
//        builder.addFirst(handlerPackage2);
//        builder.addLast(handlerPackage3);


        builder.getFirst().handler(null, null);



        System.out.println("builder.getFirst() = " + builder.getFirst());
    }
}