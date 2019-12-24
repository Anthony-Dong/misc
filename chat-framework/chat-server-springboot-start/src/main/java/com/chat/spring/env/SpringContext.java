package com.chat.spring.env;

import com.chat.spring.model.ServerConfigs;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @date:2019/11/10 16:32
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Component
public class SpringContext implements ApplicationContextAware {

    /**
     * spring容器上下文
     */
    private static ApplicationContext applicationContext = null;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.applicationContext = applicationContext;
    }

    public static ServerConfigs getServerConfigs() {
        return applicationContext.getBean(ServerConfigs.class);
    }

}
