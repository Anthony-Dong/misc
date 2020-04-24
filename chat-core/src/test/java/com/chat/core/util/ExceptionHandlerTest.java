package com.chat.core.util;

import com.chat.core.exception.ExceptionHandler;
import org.junit.Test;

public class ExceptionHandlerTest {

    @Test
    public void makeError() {
        Class<ExceptionHandler> exceptionHandlerClass = ExceptionHandler.class;

        String eee = ExceptionHandler.makeError(ExceptionHandler.class, "eee");

        System.out.println(eee);
    }
}