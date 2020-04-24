package com.chat.core.model;

import java.util.HashMap;

import static com.chat.core.model.UrlConstants.*;

/**
 * URL builder
 *
 * @date:2020/2/17 11:01
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class RouterBuilder {

    /**
     * 创建普通的 MSG 协议信息 , 不需要响应
     */
    public static String buildMessage(String sender, String receiver) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ACK_KEY, NO_ACK);
        map.put(SENDER_KEY, sender);
        map.put(RECEIVER_KEY, receiver);
        // 直接编码
        return URL.encode(new URL(MSG_PROTOCOL, null, 0, map).toString());
    }


    /**
     * 创建一个需要ACK的MSG协议
     */
    public static String buildMessageWithACK(String sender, String receiver, int id, long timeout) {
        HashMap<String, String> map = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        map.put(ACK_KEY, IS_ACK);
        builder.append(id);
        map.put(ID_KEY, builder.toString());
        builder.setLength(0);
        map.put(SENDER_KEY, sender);
        builder.append(timeout);
        map.put(TIMEOUT_KEY, builder.toString());
        map.put(RECEIVER_KEY, receiver);
        // 直接编码
        return URL.encode(new URL(MSG_PROTOCOL, null, 0, map).toString());
    }

    /**
     * RPC协议 构建
     */
    public static String buildRPC(String hostName, int port, String path, String method, int id, long timeout) {
        HashMap<String, String> map = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        builder.append(id);
        // id
        map.put(ID_KEY, builder.toString());
        builder.setLength(0);

        // method
        map.put(METHOD_KEY, method);

        // timeout
        builder.append(timeout);
        map.put(TIMEOUT_KEY, builder.toString());
        // 编码
        return URL.encode(new URL(RPC_PROTOCOL, hostName, port, path, map).toString());
    }


    /**
     * RPC协议 构建
     */
    public static String buildRPC(String hostName, int port, String path, String method, int id) {
        HashMap<String, String> map = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        builder.append(id);
        // id
        map.put(ID_KEY, builder.toString());
        builder.setLength(0);
        // method
        map.put(METHOD_KEY, method);
        // 编码
        return URL.encode(new URL(RPC_PROTOCOL, hostName, port, path, map).toString());
    }

    /**
     * 日志 协议
     */
    public static String buildLog(String path) {
        URL url = new URL(LOG_PROTOCOL, null, 0, path);
        return URL.encode(url.toString());
    }
}
