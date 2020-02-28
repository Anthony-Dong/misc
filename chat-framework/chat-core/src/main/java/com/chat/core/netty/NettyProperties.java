package com.chat.core.netty;

import java.util.Properties;

/**
 * 一个ENV对象
 *
 * @date:2020/2/24 20:15
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public final class NettyProperties extends Properties {

    private static final long serialVersionUID = 5619866598364075132L;

    public NettyProperties(Properties defaults) {
        super(defaults);
    }

    /**
     * Creates an empty property list with no default values.
     */
    public NettyProperties() {
        super();
    }

    public byte getByte(final String key, final byte defaultValue) {
        String property = getProperty(key);
        if (property == null) {
            return defaultValue;
        } else {
            return Byte.parseByte(property);
        }
    }

    public int getInt(final String key, final int defaultValue) {
        String property = getProperty(key);
        if (property == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(property);
        }
    }


    public short getShort(final String key, final short defaultValue) {
        String property = getProperty(key);
        if (property == null) {
            return defaultValue;
        } else {
            return Short.parseShort(property);
        }
    }

    public long getLong(final String key, final long defaultValue) {
        String property = getProperty(key);
        if (property == null) {
            return defaultValue;
        } else {
            return Long.parseLong(property);
        }
    }


    public String getString(final String key, final String defaultValue) {
        String property = getProperty(key);
        if (property == null) {
            return defaultValue;
        } else {
            return property;
        }
    }


    public void setString(final String string,final String value){
        setProperty(string, value);
    }

    public void setByte(final String key, final Byte value) {
        setProperty(key, value.toString());
    }


    public void setShort(final String key, final Short value) {
        setProperty(key, value.toString());
    }


    public void setInt(final String key, final Integer value) {
        setProperty(key, value.toString());
    }

    public void setLong(final String key, final Long value) {
        setProperty(key, value.toString());
    }
}
