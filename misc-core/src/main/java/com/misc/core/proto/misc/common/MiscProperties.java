package com.misc.core.proto.misc.common;

import com.misc.core.commons.PropertiesConstant;
import com.misc.core.proto.misc.common.MiscSerializableType;

import java.util.Properties;

/**
 * 一个ENV对象
 *
 * @date:2020/2/24 20:15
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class MiscProperties extends Properties {

    private static final long serialVersionUID = 5619866598364075132L;

    protected short version;

    public void setVersion(short version) {
        setShort(PropertiesConstant.CLIENT_SERVER_VERSION, version);
    }

    public Short getVersion() {
        String property = getProperty(PropertiesConstant.CLIENT_SERVER_VERSION);
        if (property == null || property.length() == 0) {
            return null;
        }
        return Short.valueOf(property);
    }

    /**
     * {@link com.misc.core.proto.misc.common.MiscSerializableType}
     *
     * @param type
     * @return
     */
    public void setSerialType(MiscSerializableType type) {
        setByte(PropertiesConstant.CLIENT_SERIALIZABLE_TYPE, type.getCode());
    }

    public MiscSerializableType getSerialType() {
        String property = getProperty(PropertiesConstant.CLIENT_SERIALIZABLE_TYPE);
        if (property == null || property.length() == 0) {
            return null;
        }
        return MiscSerializableType.getType(Byte.valueOf(property));
    }


    /**
     * Creates an empty property list with no default values.
     */
    public MiscProperties() {
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
