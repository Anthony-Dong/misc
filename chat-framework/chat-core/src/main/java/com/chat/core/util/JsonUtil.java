package com.chat.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;

/**
 * JSON 工具
 *
 * @date:2019/11/13 11:58
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class JsonUtil<T> {

    public static <T> T parseObject(String json, TypeReference<T> type) {
        return JSON.parseObject(json, type);
    }


    public static String toJSONString(Object object) {

        return JSON.toJSONString(object);
    }

}
