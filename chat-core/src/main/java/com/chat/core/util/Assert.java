package com.chat.core.util;

import com.chat.core.exception.HandlerNullPointerException;

/**
 * TODO
 *
 * @date:2019/12/26 21:21
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public final class Assert {


    public static void assertNotNull(Object object) {
        if (null == object) {
            throw HandlerNullPointerException.NULL;
        }

    }


}
