package com.misc.client.context;

import com.alibaba.fastjson.JSON;
import com.misc.client.future.MiscFuture;
import com.misc.client.future.ResponseFuture;
import com.misc.client.hander.MiscClientContext;
import com.misc.core.exception.ContextException;
import com.misc.core.exception.TimeOutException;
import com.misc.core.model.MiscPack;
import com.misc.core.model.RouterBuilder;
import com.misc.core.model.netty.ArgsUtil;
import com.misc.core.model.netty.Response;
import com.misc.core.proto.file.FileProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * 默认的实现类
 *
 * @date:2020/2/17 10:55
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class DefaultMiscClientContext extends MiscClientContext implements RpcContext, MsgContext, FileContext {

    /**
     * 本来想做缓存的,发现麻烦了
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultMiscClientContext.class);
    private static final long serialVersionUID = 6434344447972647279L;

    /**
     * 如果消息正确响应了, 但是超时了, 那么直接进入次接口, 记录日志, 用户也可以自己实现
     */
    private Consumer<Response> fallback = response -> logger.error("Fall Back : {}.", response);

    /**
     * 客户端接收到信息
     */
    @Override
    protected void onRead(Response response) {
        MiscFuture.received(response, fallback);
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onClose() {

    }

    /**
     * 发送日志,不需要返回消息,server在path路径上了. 其他信息在body里
     */
    public void sendLog(String server, String format, Object... args) {
        String logs = String.format(format, args);
        String router = RouterBuilder.buildLog(server);
        MiscPack pack = new MiscPack(router, logs.getBytes());
        context.writeAndFlush(pack);
    }

    /**
     * 发送
     */
    @Override
    public Response sendMessageBySync(String msg, String sender, String receiver) throws TimeOutException {
        return sendMessageBySync(msg, sender, receiver, requestTimeout);
    }

    /**
     * 发送消息
     */
    @Override
    public void sendMessage(String msg, String sender, String receiver) {
        String router = RouterBuilder.buildMessage(sender, receiver);
        MiscPack pack = new MiscPack(router, msg.getBytes());
        context.writeAndFlush(pack);
    }

    /**
     * 回调模式 . 出现超时再处理
     */
    @Override
    public void senderMessageWithThrowable(String msg, String sender, String receiver, Consumer<TimeOutException> consumer) {
        try {
            sendMessageBySync(msg, sender, receiver);
        } catch (TimeOutException e) {
            consumer.accept(e);
        }
    }

    /**
     * 发送消息
     *
     * @param timeout 超时时间
     */
    public Response sendMessageBySync(String msg, String sender, String receiver, long timeout) throws TimeOutException {
        // 为了更加精准 , 我们使用了 纳秒作为ID , 时间统一
        int id = MiscFuture.getCount();
        // 1.实例化router
        String router = RouterBuilder.buildMessageWithACK(sender, receiver, id, timeout);
        MiscPack pack = new MiscPack(router, msg.getBytes());
        MiscFuture future = new MiscFuture(id, pack);
        context.writeAndFlush(pack);
        return future.get(timeout);
    }


    /**
     * RPC,必须制定超时时间,构造器
     *
     * @throws TimeOutException 异常
     */
    @Override
    public Object invoke(Class<?> clazz, Method method, long timeout, Object... args) throws TimeOutException {
        MiscFuture future = sendAndInvoke(clazz, method, timeout, args);
        byte[] result = future.get(timeout).getResult();
        if (result == null || result.length == 0) {
            return null;
        }
        Class<?> returnType = method.getReturnType();
        return JSON.parseObject(result, returnType);
    }

    private MiscFuture sendAndInvoke(Class<?> clazz, Method method, long timeout, Object... args) throws TimeOutException {
        if (!clazz.isInterface()) throw new RuntimeException(String.format("代理%s对象应该是一个接口对象.", clazz));
        String path = clazz.getName();
        // name  -> echo.java.util.Map.java.util.List ,比如echo
        String name = ArgsUtil.getMethodName(method);
        // 路由信息
        int id = MiscFuture.getCount();
        String router = RouterBuilder.buildRPC(realHostName, port, path, name, id, timeout);
        // 参数信息, 转成json
        byte[] json = ArgsUtil.convertArgs(args);
        MiscPack pack;
        if (json == null || json.length == 0) {
            pack = new MiscPack(router);
        } else {
            pack = new MiscPack(router, json);
        }
        // 添加到map中
        MiscFuture future = new MiscFuture(id, pack);
        // 再发送
        context.writeAndFlush(pack);
        return future;
    }

    @Override
    public ResponseFuture invokeByAsync(Class<?> clazz, Method method, Object... args) throws TimeOutException {
        if (!clazz.isInterface()) throw new RuntimeException(String.format("代理%s对象应该是一个接口对象.", clazz));
        int id = MiscFuture.getCount();
        String path = clazz.getName();
        String name = ArgsUtil.getMethodName(method);
        String router = RouterBuilder.buildRPC(realHostName, port, path, name, id);
        byte[] json = ArgsUtil.convertArgs(args);
        MiscPack pack;
        if (json == null || json.length == 0) {
            pack = new MiscPack(router);
        } else {
            pack = new MiscPack(router, json);
        }
        Class<?> type = method.getReturnType();
        ResponseFuture future = new ResponseFuture(id, pack, type);
        context.writeAndFlush(pack);
        return future;
    }


    /**
     * 不需要回调
     */
    @Override
    public void sendFile(File file, String fileName, int split) throws ContextException {
        try {
            FileProtocol.sendFileMethod(context, getVersion(), file, fileName, false, 0, split);
        } catch (IOException e) {
            throw new ContextException(e);
        }
    }

    public void setFallback(Consumer<Response> fallback) {
        this.fallback = fallback;
    }

    public void sendFile(File file) throws ContextException {
        sendFile(file, file.getName(), 1024 * 50);
    }

    /**
     * 同步发送
     *
     * @throws ContextException
     */
    @Override
    public String sendFileSync(File file, String fileName, int split) throws ContextException {
        try {
            int count = MiscFuture.getCount();
            MiscPack pack = new MiscPack(fileName);
            // 最多等待1分钟
            MiscFuture future = new MiscFuture(count, pack);
            FileProtocol.sendFileMethod(context, version, file, file.getName(), true, count, split);
            return new String(future.get(6000).getResult());
        } catch (IOException | TimeOutException e) {
            throw new ContextException(e);
        }
    }

    public String sendFileSync(File file, int split) throws ContextException {
        return sendFileSync(file, file.getName(), split);
    }

    public String sendFileSync(File file) throws ContextException {
        return sendFileSync(file, file.getName(), 1024 * 50);
    }
}
