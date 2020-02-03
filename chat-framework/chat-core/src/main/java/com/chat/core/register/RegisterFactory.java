package com.chat.core.register;

import com.chat.core.exception.RegisterException;

import java.net.InetSocketAddress;

/**
 * @date:2020/1/21 15:39
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class RegisterFactory {

    public static final String SERVER_KEY = "server-key";


    public InetSocketAddress register() throws RegisterException {
        return new InetSocketAddress(8888);
    }

    public abstract void registerServer(InetSocketAddress address) throws RegisterException;
}
