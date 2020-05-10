package com.misc.core.register;


import com.misc.core.annotation.SPI;
import com.misc.core.exception.RegisterException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

/**
 * RegistryService. (SPI, Prototype, ThreadSafe)
 */
@SPI
public interface RegistryService {

    /**
     * 注册 输入服务器端地址 和 版本号
     */
    void register(SocketAddress address, short version) throws RegisterException;


    /**
     * 取消注册
     */
    void unregister(SocketAddress address, short version) throws RegisterException;


    /**
     * 获取版本号的 服务器地址
     */
    Set<InetSocketAddress> lookup(short version) throws RegisterException;

}