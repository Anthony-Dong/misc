package com.misc.rpc.core;

import com.misc.core.model.URL;
import lombok.Getter;
import lombok.Setter;


/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Getter
@Setter
public class RpcServiceConfig {
    private String className;

    private String version;


    public static RpcServiceConfig getServiceConfig(URL url) {
        return new RpcServiceConfig();
    }
}
