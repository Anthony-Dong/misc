package com.chat.core.model;

import java.util.Collections;
import java.util.Map;

/**
 * TODO
 *
 * @date:2020/1/21 14:07
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class URL {

    String DEFAULT_PROTOCOL = "netty://";


    private final String protocol;
    private final String username;
    private final String password;

    private final Map<String, String> parameters;

    public URL(String protocol, String username, String password, Map<String, String> parameters) {
        this.protocol = protocol;
        this.username = username;
        this.password = password;
        this.parameters = parameters;
    }

    public URL(String protocol, String username, String password) {
        this(protocol, username, password, Collections.emptyMap());
    }
}
