package com.misc.core.proto.misc.common;

import com.misc.core.util.StringUtils;

import java.util.Properties;

/**
 * todo
 *
 * @date: 2020-05-18
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class TypeProperties extends Properties {

    private static final long serialVersionUID = 2690251864509886264L;


    public String getString(final String key, final String defaultValue) {
        return getProperty(key, defaultValue);
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

    public void setString(final String key, final String value) {
        setProperty(key, value);
    }


    public void setByte(final String key, final byte value) {
        setProperty(key, String.valueOf(value));
    }

    public void setShort(final String key, final short value) {
        setProperty(key, String.valueOf(value));
    }

    public void setInt(final String key, final int value) {
        setProperty(key, String.valueOf(value));
    }

    public void setLong(final String key, final long value) {
        setProperty(key, String.valueOf(value));
    }


    public int getIntProperty(String key) {
        String property = super.getProperty(key);
        return StringUtils.isEmpty(property) ? 0 : Integer.parseInt(property);
    }

    public long getLongProperty(String key) {
        String property = super.getProperty(key);
        return StringUtils.isEmpty(property) ? 0 : Long.parseLong(property);
    }

    public short getShortProperty(String key) {
        String property = super.getProperty(key);
        return StringUtils.isEmpty(property) ? 0 : Short.parseShort(property);
    }

    public String getStringProterty(String key) {
        return super.getProperty(key);
    }

    public byte getByteProperty(String key) {
        String property = super.getProperty(key);
        return StringUtils.isEmpty(property) ? 0 : Byte.parseByte(property);
    }
}
