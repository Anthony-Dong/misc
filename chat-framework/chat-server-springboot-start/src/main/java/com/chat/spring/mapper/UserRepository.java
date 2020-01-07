package com.chat.spring.mapper;

import com.chat.spring.pojo.UserDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @date:2020/1/7 16:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface UserRepository extends JpaRepository<UserDo, Long> {


    @Query("select u.phone from UserDo u where u.phone=?1")
    Long findByPhone(Long phone);




}
