package com.misc.server.netty;

import com.misc.core.listener.MiscEvent;
import com.misc.core.listener.MiscEventListener;
import com.misc.core.listener.MiscEventType;
import com.misc.core.model.MiscPack;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.Executor;

/**
 * 服务器 通用处理器
 */
public class MiscServerHandler extends SimpleChannelInboundHandler<MiscPack> {

    private static final Logger logger = LoggerFactory.getLogger(MiscServerHandler.class);

    private final MiscEventListener listener;

    private final Executor executor;

    @Override
    public boolean isSharable() {
        return true;
    }

    MiscServerHandler(MiscEventListener listener, Executor executor) {
        super(true);
        this.listener = listener;
        this.executor = executor;
    }


    /**
     * 不需要传递,浪费时间罢了
     *
     * @param evt 心跳事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            logger.error("[Misc-Server] Receive heart beat timeout, remote address:{}.", ctx.channel().remoteAddress());
            ctx.close();
        }
    }

    /**
     * 执行断开业务的逻辑
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        listener.onChatEvent(new MiscEvent() {
            @Override
            public MiscEventType eventType() {
                return MiscEventType.SERVER_HANDLER_REMOVED;
            }

            @Override
            public Object event() {
                return ctx;
            }
        });
    }


    /**
     * 客户端连接成功
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            ctx.fireChannelActive();
        } finally {
            listener.onChatEvent(new MiscEvent() {
                @Override
                public MiscEventType eventType() {
                    return MiscEventType.SERVER_CHANNEL_REGISTERED;
                }

                @Override
                public Object event() {
                    return ctx;
                }
            });
        }
    }

    /**
     * 如果发生异常就关闭
     *
     * @throws Exception 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error("[Misc-Server] Happened exception the remote address: {} will be disconnected because of :{}.", ctx.channel().remoteAddress(), cause.getMessage());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MiscPack msg) throws Exception {
        executor.execute(() -> {
            try {
                listener.onChatEvent(new MiscEvent() {
                    @Override
                    public MiscEventType eventType() {
                        return MiscEventType.SERVER_READ;
                    }

                    @Override
                    public Object event() {
                        SocketAddress address = ctx.channel().remoteAddress();
                        msg.setAddress(address);
                        return msg;
                    }
                });
            } catch (Exception e) {
                logger.error("[Misc-Server] Happened exception client-ip: {}, exception: {}.", ctx.channel().remoteAddress(), e.getMessage());
                try {
                    // todo
                    listener.onChatEvent(new MiscEvent() {
                        @Override
                        public MiscEventType eventType() {
                            return MiscEventType.SERVER_HANDLER_REMOVED;
                        }

                        @Override
                        public Object event() {
                            return ctx;
                        }
                    });
                } catch (Exception e1) {
                    //
                }
            } finally {
                msg.release();
            }
        });
    }

}
