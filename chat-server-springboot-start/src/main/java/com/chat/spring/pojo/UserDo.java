package com.chat.spring.pojo;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户表
 *
 * @date:2020/1/6 10:43
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Table(name = "chat_user",
        indexes = {
                @Index(columnList = "phone_num")
        })
@Entity
public class UserDo {
    public UserDo() {
    }

    /**
     * 系统生成的ID
     */
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(name = "name", length = 20)
    private String name;


    @Column(name = "password", length = 40)
    private String password;


    @Column(name = "salt", length = 40)
    private String salt;


    @Column(name = "phone_num", unique = true)
    private Long phone;

    /**
     * 1.在线
     * 2.离线
     */
    @Column(name = "state")
    private Integer state;


    @Column(name = "login_time")
    private Date loginTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}
