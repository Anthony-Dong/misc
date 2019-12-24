package com.chat.core.util;

/**
 *
 *
 * @date:2019/12/24 22:22
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class ObjectConvertUtil {

    static <T> T convert(Object o) {
        return (T) o;
    }

    public static void main(String[] args) {
        Object str = new String("11111");
        String convert = ObjectConvertUtil.<String>convert(str);
        System.out.println(convert);
    }

}
