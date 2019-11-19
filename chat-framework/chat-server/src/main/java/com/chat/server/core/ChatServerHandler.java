package com.chat.server.core;


import com.alibaba.fastjson.TypeReference;
import com.chat.core.listener.ChatBootEvent;
import com.chat.core.listener.ChatBootListener;
import com.chat.core.listener.ChatBootSource;
import com.chat.core.model.ChatEntity;
import com.chat.core.model.NPack;
import com.chat.core.packutil.Constants;
import com.chat.core.util.JsonUtil;
import com.chat.server.util.RedisPool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务器 通用处理器
 */
public class ChatServerHandler extends ChannelInboundHandlerAdapter {


    private static Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);


    private ChannelGroup groups;

    private RedisPool redisPool;

    private AtomicInteger totalConnection;

    private ChatBootListener listener;

    private InetSocketAddress address;


    public ChatServerHandler(RedisPool redisPool, AtomicInteger totalConnection, ChatBootListener listener, InetSocketAddress address) {
        this.groups = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        this.redisPool = redisPool;
        this.totalConnection = totalConnection;
        this.listener = listener;
        this.address = address;
    }


    /**
     * 执行断开业务的逻辑
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //通知其他管道
        super.handlerRemoved(ctx);
        // 减少 连接数
        totalConnection.decrementAndGet();
        // 移除 连接
        groups.remove(ctx.channel());
    }

    /**
     * 注册成功 像客户端发送一个响应
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        NPack reg = new NPack("[服务器-" + address.getPort() + "]", "注册成功 服务器收到连接请求 , 客户端的IP  : " + ctx.channel().remoteAddress());
        ctx.channel().writeAndFlush(reg).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    groups.add(ctx.channel());
                    totalConnection.incrementAndGet();
                    logger.info("[服务器-{}] 客户端 : {} 注册成功 ", address.getPort(), ctx.channel().remoteAddress());
                }
            }
        });
    }

    /**
     * 如果发生异常就关闭
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("[服务器-{}] 客户端 : {} 异常信息 : {}  ", address.getPort(), ctx.channel().remoteAddress(), cause.getMessage());
        // 连接异常 ,直接断开连接
        if (cause instanceof IOException) {
            // 发送一个事件 , 就是远程客户端 断开那个事件 ,就是远程客户端的IP 值
            listener.onChatBootEvent(new ChatBootEvent(new ChatBootSource() {
                @Override
                public String hasOtherMsg() {
                    return ctx.channel().remoteAddress().toString();
                }
            }));
            //关闭连接 -> handlerRemoved() 方法
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * 读取客户端消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof NPack) {
            NPack messages = (NPack) msg;
            logger.info("[服务器-{}] 接收到客户端 : {}  的信息  : {}  ", address.getPort(), ctx.channel().remoteAddress(), messages);
            // 不是心跳包 ..  处理
            if (!messages.getRouter().equals(Constants.HEART_BEAT_NPACK_ROUTER)) {
                processNPack(messages);
                ctx.channel().writeAndFlush(messages);
            }
        } else {
            // 不是我们的我们什么也不错
            super.channelRead(ctx, msg);
        }
    }


    public void processNPack(NPack messaage) {

        Jedis jedis = redisPool.get();

        String json = messaage.getJson();

        ChatEntity chatEntity = JsonUtil.parseObject(json, new TypeReference<ChatEntity>() {
        });

        String receiver = chatEntity.getReceiver();

        jedis.zadd(receiver, 1573660000000L - chatEntity.getTimestamp(), json);

        // TODO: 2019/11/13  保存消息

        redisPool.remove(jedis);
        messaage = null;
    }
}
