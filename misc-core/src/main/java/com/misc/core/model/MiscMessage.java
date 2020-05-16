package com.misc.core.model;

import java.io.Serializable;

/**
 * todo
 *
 * @date: 2020-05-16
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public abstract class MiscMessage implements Serializable {
    private static final long serialVersionUID = -6681235176564580005L;

    public abstract void release();
}
