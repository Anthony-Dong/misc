package com.chat.spring.mapper;

import com.chat.spring.pojo.MessageDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @date:2020/1/7 16:18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface MessageRepository extends JpaRepository<MessageDo, Long> {


    @Query("SELECT m.id FROM MessageDo m WHERE m.senderId=?1")
    Long findIDBySenderId(Long senderID);

}
