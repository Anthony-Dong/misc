package com.chat.spring.service;

import com.chat.core.loadbalance.LoadBalance;
import com.chat.core.netty.PropertiesConstant;
import com.chat.core.register.RegisterFactory;
import com.chat.core.util.EncodeUtil;
import com.chat.core.util.Pair;
import com.chat.spring.mapper.MessageRepository;
import com.chat.spring.mapper.UserRepository;
import com.chat.spring.pojo.UserDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetSocketAddress;
import java.util.*;

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


    @Qualifier(value = ChatServerConfiguration.chatRedisTemplate)
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Qualifier(value = ChatServerConfiguration.LOAD_BALANCE)
    @Autowired
    private LoadBalance loadBalance;


    @Transactional
    public Long register(UserDo userDo) {
        Long phone = userDo.getPhone();
        if (null == userDo.getPhone()) {
            return null;
        }
        Long byPhone = userRepository.findByPhone(phone);

        if (null == byPhone) {
            //userDo.setId(phone);

            String password = userDo.getPassword();
            String uuid = UUID.randomUUID().toString();
            userDo.setSalt(uuid);


            String ok = password + uuid;
            // 加密的
            String up = EncodeUtil.getMD5(ok);


            userDo.setPassword(up);


            userRepository.save(userDo);
            return phone;
        } else {
            return null;
        }
    }

    //  user - password+ salt mdd = password
    public Optional<InetSocketAddress> login(UserDo userDo) {
        UserDo user = userRepository.findByName(userDo.getName());

        if (user == null) {
            return Optional.empty();
        }

        String pw = user.getPassword();
        String salt = user.getSalt();


        String upw = userDo.getPassword();

        String out = upw + salt;


        String up = EncodeUtil.getMD5(out);
        if (pw.equals(up)) {

            BoundHashOperations<String, String, Object> hash = redisTemplate.boundHashOps(PropertiesConstant.CLIENT_REGISTER_KEY);

            Map<String, Object> entries = hash.entries();


            if (entries == null || entries.isEmpty()) {
                return Optional.empty();
            }

            HashSet<Pair<InetSocketAddress, Integer>> set = new HashSet<>(entries.size());
            entries.forEach((s, o) -> {
                long time = System.currentTimeMillis();
                long l = Long.parseLong(s);
                if (time - l < 30000) {
                    try {
                        Pair<InetSocketAddress, Integer> pair = (Pair<InetSocketAddress, Integer>) o;
                        set.add(pair);
                    } catch (Exception e) {
                        //
                    }
                }
            });

            return loadBalance.loadBalance(set);
        } else {
            return Optional.empty();
        }
    }
}
