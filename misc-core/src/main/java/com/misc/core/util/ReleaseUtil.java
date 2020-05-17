package com.misc.core.util;

import com.misc.core.model.Releasable;

/**
 * todo
 *
 * @date: 2020-05-17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ReleaseUtil {

    public static void release(Releasable obj) {
        obj.release();
    }
}
