package com.chat.server.spi;

import com.chat.core.annotation.SPI;
import com.chat.core.exception.HandlerException;
import com.chat.core.model.NPack;

/**
 * 保存 接收到的package
 */
@SPI
public interface SaveReceivePackage {

    /**
     * 保存 数据包的唯一拓展接口
     *
     * @param pack NPack 数据包
     * @throws HandlerException 异常
     */
    void doSave(NPack pack) throws HandlerException;
}
