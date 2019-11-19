package com.chat.client.model;


import com.chat.core.model.NPack;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * template 保存了 每一个 ChannelFuture 的信息
 *
 * @date:2019/11/10 18:38
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ChatTemplate {

    /**
     * 长度等于0  直接抛出异常了 启动失败
     *
     * @param channelFuture
     */
    public ChatTemplate(ChannelFuture... channelFuture) {

        for (ChannelFuture future : channelFuture) {
            this.channelFutures.add(future);
        }

        this.size = this.channelFutures.size();
    }

    /**
     * 保存了所有信息 的 Futures ,防止 某个客户端down掉
     */
    private List<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();


    public List<ChannelFuture> getChannelFutures() {
        return channelFutures;
    }


    public int getSize() {
        return size;
    }


    private int size;

    /**
     * 这里选择的是 负载均衡算法  message % 长度 ,这里绝对会出现多线程的问题 所以此时我们抛出异常
     *
     * @param message
     * @param listener
     */
    public void write(NPack message, GenericFutureListener listener) throws Exception {
        int index = 0;
        try {
            index = message.hashCode() % this.size;
            this.channelFutures.get(index).channel().writeAndFlush(message).addListener(listener);
        } catch (Exception e) {
            //如果出现了 是 ArrayIndexOutOfBoundsException , 则不是因为 channelFutures 整个 channelFutures的问题
            if (e instanceof ArrayIndexOutOfBoundsException) {
                throw new RuntimeException(e.getMessage());
            } else {
                // 否则移除
                doException(this.channelFutures.get(index));
            }
        }
    }


    /**
     * 移除错误的
     *
     * @param future
     */
    private void doException(ChannelFuture future) {
        System.out.println("doException");

        this.channelFutures.remove(future);
    }
}
