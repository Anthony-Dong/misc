package com.chat.server.spi;

import com.chat.core.exception.HandlerException;
import com.chat.core.model.NPack;

/**
 * TODO
 *
 * @date:2020/1/7 20:54
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class DefaultSaveReceivePackage implements SaveReceivePackage {


    /**
     * 保存 数据包的唯一拓展接口
     *
     * @param pack NPack 数据包
     * @throws HandlerException 异常
     */
    @Override
    public void doSave(NPack pack) throws HandlerException {
        System.out.println(pack);
    }
}
