package com.chat.spring.pojo;

import javax.persistence.*;
import java.util.Date;

/**
 * 不做外键的原因是JPA查询懒快查询的问题 ， 基本索引足够了
 *
 * @date:2020/1/7 15:53
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Table(name = "chat_msg", indexes = {
        @Index(columnList = "receiver_id"),
        @Index(columnList = "sender_id"),
})
@Entity
public class MessageDo {
    public MessageDo() {
    }

    @Id
    private Long id;

    @Column(name = "receiver_id")
    private Long receiverId;


    @Column(name = "sender_id")
    private Long senderId;


    private String message;


    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
