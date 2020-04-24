package com.chat.client.context;

import com.chat.core.exception.ContextException;
import com.chat.core.model.netty.Response;
import com.chat.core.netty.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @date:2020/2/20 21:41
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface FileContext {

    void sendFile(File file, String fileName, int splitSize) throws ContextException;


    String sendFileSync(File file, String fileName, int splitSize) throws ContextException;

}
