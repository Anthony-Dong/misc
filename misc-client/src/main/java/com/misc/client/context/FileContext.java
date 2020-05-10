package com.misc.client.context;

import com.misc.core.exception.ContextException;

import java.io.File;

/**
 * @date:2020/2/20 21:41
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface FileContext {

    void sendFile(File file, String fileName, int splitSize) throws ContextException;

    String sendFileSync(File file, String fileName, int splitSize) throws ContextException;

}
