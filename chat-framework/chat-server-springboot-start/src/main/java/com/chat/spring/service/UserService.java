package com.chat.spring.service;

import com.chat.spring.mapper.MessageRepository;
import com.chat.spring.mapper.UserRepository;
import com.chat.spring.pojo.UserDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @date:2020/1/7 16:20
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private MessageRepository messageRepository;


    @Transactional
    public Long register(UserDo userDo) {
        Long phone = userDo.getPhone();
        if (null == userDo.getPhone()) {
            return null;
        }

        Long byPhone = userRepository.findByPhone(phone);

        if (null == byPhone) {
            userDo.setId(phone);
            userRepository.save(userDo);
            return phone;
        } else {
            return null;
        }
    }


}
