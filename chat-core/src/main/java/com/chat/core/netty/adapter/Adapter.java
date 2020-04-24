package com.chat.core.netty.adapter;

import com.chat.core.model.NPack;
import com.chat.core.netty.PackageEncoder;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @date:2020/3/31 17:09
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Adapter {
    MessageToByteEncoder<NPack> getEncodeAdapter(byte type) {
        return new PackageEncoder((short) 0, type);
    }
}
