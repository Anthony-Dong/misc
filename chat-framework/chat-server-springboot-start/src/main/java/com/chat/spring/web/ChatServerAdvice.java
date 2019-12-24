package com.chat.spring.web;

import com.chat.core.exception.ChatHttpRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *
 * @date:2019/11/13 16:24
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@ControllerAdvice(assignableTypes = {ChatServerController.class})
public class ChatServerAdvice {


    /**
     * 直接返回 400状态码
     * @param e
     * @return
     */
    @ExceptionHandler(value = {ChatHttpRequestException.class})
    public ResponseEntity<Void> handlerException(ChatHttpRequestException e) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


}
