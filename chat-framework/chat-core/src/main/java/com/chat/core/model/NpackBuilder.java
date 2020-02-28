package com.chat.core.model;

import com.chat.core.annotation.NotNull;
import com.chat.core.util.FileUtil;
import com.chat.core.util.JsonUtil;
import com.chat.core.util.RouterUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @date:2020/2/17 10:57
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
@Deprecated
public class NpackBuilder {



    public static NPack buildWithStringBody(@NotNull String sender, @NotNull String receiver, @NotNull String msg) {
        String router = RouterUtil.getRouterByString(RouterUtil.STRING_TYPE, sender, receiver);
        return new NPack(router, msg.getBytes());
    }

    public static NPack buildWithByteBody(@NotNull String sender, @NotNull String receiver, String fileName, @NotNull byte[] msg) {
        String router = RouterUtil.getRouterByFile(RouterUtil.BYTE_TYPE, sender, receiver, fileName);
        return new NPack(router, msg);
    }

    public static List<NPack> buildWithByteBody(@NotNull String sender, @NotNull String receiver, File file, int slice) {
        List<byte[]> list = null;
        try {
            list = FileUtil.cuttingFile(file, slice);

        } catch (Exception e) {
            //
        }
        if (list == null || list.size() == 0) {
            return Collections.emptyList();
        }
        List<NPack> nPackes = new ArrayList<>(list.size());

        String fileName = file.getName();
        list.forEach(e -> nPackes.add(buildWithByteBody(sender, receiver, fileName, e)));
        list = null;
        return nPackes;
    }


    public static <T> NPack buildWithJsonBody(@NotNull String sender, @NotNull String receiver, @NotNull T msg) {
        String classname = msg.getClass().getName();
        String router = RouterUtil.getRouterByJson(RouterUtil.JSON_TYPE, sender, receiver, classname);
        String json = JsonUtil.toJSONString(msg);
        return new NPack(router, json.getBytes());
    }


}
