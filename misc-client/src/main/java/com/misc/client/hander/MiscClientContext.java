package com.misc.client.hander;

import com.misc.client.netty.MiscClient;
import com.misc.core.context.AbstractContext;
import com.misc.core.model.netty.Response;
import com.misc.core.commons.Constants;
import com.misc.core.util.NetUtils;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CountDownLatch;

import static com.misc.core.commons.PropertiesConstant.*;

/**
 * 客户端上下文
 *
 * @date:2019/12/24 22:51
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class MiscClientContext extends AbstractContext {

    private static final long serialVersionUID = -8865564334594246455L;

    /**
     * 全局唯一的context 对象
     */
    protected ChannelHandlerContext context;

    /**
     * 真实的ip
     */
    protected String realHostName = NetUtils.filterLocalHost(getHost());

    /**
     * 心跳时间
     */
    protected int heartInterval = Constants.DEFAULT_CLIENT_HEART_INTERVAL;

    /**
     * 启动器
     */
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * 阻塞过程
     *
     * @return ChannelHandlerContext
     */
    public final ChannelHandlerContext getContext() {
        return context;
    }

    /**
     * 只允许开发者设置
     *
     * @param context ChannelHandlerContext
     */
    final void setContext(ChannelHandlerContext context) {
        this.context = context;
    }


    /**
     * 客户端接收到信息
     *
     * @param context MiscPack
     */
    protected abstract void onRead(Response context);

    public CountDownLatch getLatch() {
        return latch;
    }

    @Override
    public void setHeartInterval(int heartInterval) {
        setInt(CLIENT_HEART_INTERVAL, heartInterval);
        this.heartInterval = heartInterval;
    }

    // 为了关闭掉服务器
    protected MiscClient client;

    public final void setClient(MiscClient client) {
        this.client = client;
    }

    // 清空引用
    private void release() {
        if (client != null) {
            client.stop();
            client = null;
        }
    }

//    /**
//     * 收到事件后, 先释放掉 client.
//     */
//    @Override
//    public void onShutdown() {
//        try {
//            release();
//        } finally {
//            onClose();
//        }
//    }
//
//    @Override
//    public void onBootstrap() {
//        onStart();
//    }

    protected abstract void onStart();

    protected abstract void onClose();
}
