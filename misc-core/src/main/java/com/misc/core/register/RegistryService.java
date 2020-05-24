package com.misc.core.register;


import com.misc.core.annotation.SPI;
import com.misc.core.exception.RegisterException;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * RegistryService. (SPI, Prototype, ThreadSafe)
 */
@SPI
public interface RegistryService {

    /**
     * 注册服务器
     */
    void register(RemoteInfo info) throws RegisterException;


    /**
     * 获取服务器地址
     */
    Set<RemoteInfo> lookup(RemoteInfo info) throws RegisterException;
}