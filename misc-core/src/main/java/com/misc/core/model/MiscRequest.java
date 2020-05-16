package com.misc.core.model;

import com.misc.core.proto.ProtocolType;
import lombok.Getter;
import lombok.Setter;

/**
 * 请求
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Setter
@Getter
public abstract class MiscRequest extends MiscMessage {
    private static final long serialVersionUID = -5557855661541199464L;
    /**
     * 请求头
     */
    protected URL url;

    /**
     * 服务版本号
     */
    protected short serverVersion;

    /**
     * 时间搓
     */
    protected long timeStamp;

    /**
     * 进程id
     */
    protected int pid;

    /**
     * key
     */
    protected long key;


}
