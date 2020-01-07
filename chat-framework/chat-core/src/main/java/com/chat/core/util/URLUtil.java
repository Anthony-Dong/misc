package com.chat.core.util;

import com.chat.core.annotation.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * TODO
 *
 * @date:2019/12/26 22:16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class URLUtil {

    // 编码
    public static String encode(@NotNull String url, String charset) {
        try {
            return URLEncoder.encode(url, charset);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static final String UTF8 = "UTF-8";


    public static String encode(@NotNull String url) {
        return encode(url, UTF8);
    }

    public static String decode(@NotNull String url, String charset) {
        try {
            return URLDecoder.decode(url, charset);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    // 解码
    public static String decode(@NotNull String url) {
        return decode(url, UTF8);
    }

}
