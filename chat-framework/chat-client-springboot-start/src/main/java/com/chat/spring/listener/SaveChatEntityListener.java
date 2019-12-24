package com.chat.spring.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 监听保存时间
 *
 * @date:2019/11/13 17:01
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Component
public class SaveChatEntityListener implements ApplicationListener<SaveChatEntityEvent> {


    @Override
    public void onApplicationEvent(SaveChatEntityEvent event) {

        // TODO: 2019/11/13   需要保存到数据库
        System.out.println("保存成功 : "+event.getSource() + " : " + Thread.currentThread().getName());
    }
}
