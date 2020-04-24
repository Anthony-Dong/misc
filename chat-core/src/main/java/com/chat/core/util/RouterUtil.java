package com.chat.core.util;

import com.chat.core.annotation.MayEmpty;
import com.chat.core.annotation.NotNull;
import java.util.Properties;

import static com.chat.core.util.Assert.*;
import static com.chat.core.util.URLUtil.*;

/**
 * {@link com.chat.core.model.NPack}
 *
 * @date:2019/12/25 9:15
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Deprecated
public final class RouterUtil {

    public static final String TYPE = "type";

    public static final String SENDER = "sender";

    public static final String RECEIVER = "receiver";

    public static final String TIMESTAMP = "timestamp";

    public static final String STRING_TYPE = "string";

    /**
     * 二进制文本
     */
    public static final String BYTE_TYPE = "byte";


    /**
     * FILE_NAME
     */
    public static final String FILE_NAME = "filename";


    public static final String JSON_TYPE = "json";


    public static final String JSON_CLASS_NAME = "classname";


    private static final char DELIMITER_1 = '&';
    private static final char DELIMITER_2 = '=';


    /**
     * URL 编码格式   name=xiaoli&qq&s -> 先解码 -> 获取值
     *
     * @param router 请求路由解析
     * @return Properties 当非法返回一个空的对象
     */
    @MayEmpty
    public static Properties convertRouter(@NotNull String router) {
        String url = decode(router);

        // 进来就要处理,  如果异常就返回空了 , 所以这里做判断
        assertNotNull(url);

        String[] split = StringUtils.split(url, DELIMITER_1);
        // 非法字符串
        if (split.length == 1) {
            return null;
        }

        Properties properties = new Properties();

        for (String s : split) {
            String[] kv = StringUtils.split(s, DELIMITER_2);
            if (kv.length == 1) {
                properties = null;
                return null;
            }
            properties.put(kv[0], kv[1]);
        }
        return properties;
    }


    /**
     * 这里没有考虑URL编码
     *
     * @param type     消息类型
     * @param sender   发送者ID
     * @param receiver 消息接受者ID
     * @return 路由
     */
    public static String getRouterByString(String type, String sender, String receiver) {
        assertNotNull(type);
        assertNotNull(sender);
        assertNotNull(receiver);

        StringBuilder builder = new StringBuilder();
        builder.append(TYPE).append(DELIMITER_2).append(type).append(DELIMITER_1)
                .append(SENDER).append(DELIMITER_2).append(sender).append(DELIMITER_1)
                .append(RECEIVER).append(DELIMITER_2).append(receiver);
        String router = builder.toString();
        // 清空
        builder = null;

        return encode(router);
    }


    public static String getRouterByFile(String type, String sender, String receiver, String filename) {
        assertNotNull(type);
        assertNotNull(sender);
        assertNotNull(receiver);
        assertNotNull(filename);

        StringBuilder builder = new StringBuilder();
        builder.append(TYPE).append(DELIMITER_2).append(type).append(DELIMITER_1)
                .append(SENDER).append(DELIMITER_2).append(sender).append(DELIMITER_1)
                .append(RECEIVER).append(DELIMITER_2).append(receiver).append(DELIMITER_1)
                .append(FILE_NAME).append(DELIMITER_2).append(filename);
        String router = builder.toString();
        builder = null;

        return encode(router);
    }


    public static String getRouterByJson(String type, String sender, String receiver, String className) {
        assertNotNull(type);
        assertNotNull(sender);
        assertNotNull(receiver);
        assertNotNull(className);

        StringBuilder builder = new StringBuilder();
        builder.append(TYPE).append(DELIMITER_2).append(type).append(DELIMITER_1)
                .append(SENDER).append(DELIMITER_2).append(sender).append(DELIMITER_1)
                .append(RECEIVER).append(DELIMITER_2).append(receiver).append(DELIMITER_1)
                .append(JSON_CLASS_NAME).append(DELIMITER_2).append(className);
        String router = builder.toString();
        builder = null;

        return encode(router);
    }


    public static StringBuilder add(StringBuilder builder, String key, String value) {
        int length = builder.length();
        if (length != 0) {
            char c = builder.charAt(length - 1);
            if (c == DELIMITER_1) {
                // 以 & 结尾
                return builder.append(key).append(DELIMITER_2).append(value);
            } else {
                // 没有以&结尾
                return builder.append(DELIMITER_1).append(key).append(DELIMITER_2).append(value);
            }
        }
        return builder.append(key).append(DELIMITER_2).append(value);
    }

}
