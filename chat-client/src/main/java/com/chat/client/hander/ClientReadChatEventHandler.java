package com.chat.client.hander;

import com.chat.client.netty.ChantClientHandler;
import com.chat.core.exception.HandlerException;
import com.chat.core.handler.ChatEventHandler;
import com.chat.core.listener.ChatEvent;
import com.chat.core.model.NPack;
import com.chat.core.model.URL;
import com.chat.core.model.netty.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * {@link ChantClientHandler#channelRead} 方法处理器
 *
 * @date:2019/12/24 19:51
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ClientReadChatEventHandler implements ChatEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientReadChatEventHandler.class);

    private final ChatClientContext chatClientContext;

    ClientReadChatEventHandler(ChatClientContext chatClientContext) {
        this.chatClientContext = chatClientContext;
    }

    @Override
    public void handler(ChatEvent event) throws HandlerException {
        Object obj = event.event();
        Response response = convert(obj);
        if (response != null) {
            chatClientContext.onRead(response);
            logger.debug("[客户端] ReceiveResponse : {}.", response.getUrl().toString());
        } else {
            logger.error("[客户端] {} 解码异常.", obj);
        }
    }


    /**
     * 转换
     */
    private static Response convert(Object obj) {
        NPack pack = (NPack) obj;
        Response response = new Response(URL.valueOfByDecode(pack.getRouter()), pack.getBody(), pack.getTimestamp());
        pack.release();
        return response;
    }
}
