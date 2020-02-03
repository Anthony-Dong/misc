package com.chat.spring.web;

import com.chat.core.model.HttpResponse;
import com.chat.core.util.Pair;
import com.chat.spring.pojo.UserDo;
import com.chat.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * @date:2020/1/7 16:36
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@RequestMapping("/api/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/reg")
    public HttpResponse reg(UserDo userDo) {
        Long register = userService.register(userDo);
        if (null == register) {
            return HttpResponse.fail(null);
        } else {
            return HttpResponse.success(register);
        }
    }

    @PostMapping("/log")
    public HttpResponse log(UserDo userDo) {
        Optional<InetSocketAddress> optional = userService.login(userDo);
        if (optional.isPresent()) {
            return HttpResponse.success(optional);
        }
        return HttpResponse.fail(optional);
    }
}
