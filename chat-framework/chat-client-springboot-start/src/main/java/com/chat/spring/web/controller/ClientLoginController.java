package com.chat.spring.web.controller;


import com.chat.core.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 * @date:2019/11/14 14:23
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@RestController
@RequestMapping("/chat/client/login")
public class ClientLoginController {


    @PostMapping("/do")
    public void log(@RequestBody User user, HttpServletRequest request) {

        // TODO: 2019/11/15  类似于手机验证码 -> 获取到手机号

        String remoteHost = request.getRemoteHost();

    }



}
