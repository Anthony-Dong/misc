package com.chat.conf.web;

import com.chat.conf.exception.ChatHttpRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @date:2019/11/13 12:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@ResponseBody
@ControllerAdvice(assignableTypes = {ConfController.class})
public class HandlerException {


    /**
     * response  的响应码为 400 , 然后根据状态码判断
     */
    @ExceptionHandler(ChatHttpRequestException.class)
    public ResponseEntity<Void> handler(ChatHttpRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
