package com.chat.spring.web;

import com.chat.spring.pojo.UserDo;
import com.chat.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @date:2020/1/7 16:36
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@RequestMapping("/api/user")
@RestController
public class UserController {

    @Autowired
    private UserService server;

    @PostMapping("/reg")
    public ResponseEntity<Long> reg(@RequestBody UserDo userDo) {
        Long register = server.register(userDo);
        if (null == register) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(register);
        }
    }
}
