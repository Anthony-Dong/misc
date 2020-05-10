package com.misc.client.hander;

import com.misc.client.netty.ChantClientHandler;
import com.misc.core.exception.HandlerException;
import com.misc.core.handler.MiscEventHandler;
import com.misc.core.listener.MiscEvent;
import com.misc.core.model.MiscPack;
import com.misc.core.model.URL;
import com.misc.core.model.netty.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * {@link ChantClientHandler#channelRead} 方法处理器
 *
 * @date:2019/12/24 19:51
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientReadMiscEventHandler implements MiscEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientReadMiscEventHandler.class);

    private final MiscClientContext miscClientContext;

    ClientReadMiscEventHandler(MiscClientContext miscClientContext) {
        this.miscClientContext = miscClientContext;
    }

    @Override
    public void handler(MiscEvent event) throws HandlerException {
        Object obj = event.event();
        Response response = convert(obj);
        if (response != null) {
            miscClientContext.onRead(response);
            logger.debug("[Misc-Client] Receive message success url: {}.", response.getUrl().toString());
        } else {
            logger.error("[Misc-Client] The {} serialize error.", obj);
        }
    }


    /**
     * 转换
     */
    private static Response convert(Object obj) {
        MiscPack pack = (MiscPack) obj;
        Response response = new Response(URL.valueOfByDecode(pack.getRouter()), pack.getBody(), pack.getTimestamp());
        pack.release();
        return response;
    }
}
