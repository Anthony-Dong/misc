package com.misc.core.model;

import com.misc.core.func.FunctionType;
import com.misc.core.proto.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 响应
 *
 * @date: 2020-05-15
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class MiscResponse extends MiscMessage {

    private static final long serialVersionUID = -7509671817822895589L;
    /**
     * 请求头
     */
    private URL url;

    /**
     * 协议类型
     */
    private FunctionType protocolType;

    /**
     * 服务版本号
     */
    private short serverVersion;

    /**
     * 时间搓
     */
    private long timeStamp;

    /**
     * 进程id
     */
    private int pid;

}
