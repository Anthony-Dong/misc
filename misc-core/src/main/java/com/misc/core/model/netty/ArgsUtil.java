package com.misc.core.model.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * ARG 的工具类
 *
 * @date:2020/2/17 17:15
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class ArgsUtil {

    /**
     * 如果返回空代表无数据,请注意空指针
     */
    public static byte[] convertArgs(Object... args) {
        if (args == null || args.length == 0) return null;
        List<Arg> list = new ArrayList<>(args.length);
        for (int i = 0; i < args.length; i++) {
            Arg arg = new Arg();
            arg.setIndex(i);
            arg.setClazz(args[i].getClass());
            // 这里做的目的是为了 参数一致
            arg.setValue(JSON.toJSONString(args[i]));
            list.add(arg);
        }
        return JSON.toJSONBytes(list);
    }

    /**
     * 比如 echo(string name) 输出 ->  echo.java.long.string
     */
    public static String getMethodName(Method method) {
        StringBuilder builder = new StringBuilder(10);
        String name = method.getName();
        builder.append(name);
        for (Parameter parameter : method.getParameters()) {
            String pName = parameter.getType().getName();
            builder.append('.').append(pName);
        }
        return builder.toString();
    }

    /**
     * 一个类型转换器
     */
    private static final Type TYPE = new TypeReference<List<Arg>>() {
    }.getType();

    /**
     * 转换 , 如果为空就抛出异常
     * 处理流程, 如果原类型不支持, 则接口类型, 如果接口类型还是不支持就OBJ了
     *
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("all")
    public static Object[] convert(byte[] json, Method method) throws IllegalArgumentException {
        List<Arg> args = JSON.parseObject(json, TYPE);
        if (args == null || args.size() != method.getParameters().length) {
            throw new IllegalArgumentException("传入参数和实际参数长度不一致");
        }
        Object[] arr = new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            Arg arg = args.get(i);
            Object object = null;
            try {
                object = JSON.parseObject(arg.getValue(), arg.getClazz());
            } catch (Exception e) {
                try {
                    Parameter[] parameters = method.getParameters();
                    object = JSON.parseObject(arg.getValue(), parameters[i].getType());
                } catch (Exception e1) {
                    object = JSON.parseObject(arg.getValue(), Object.class);
                }
            }
            arr[i] = object;
        }
        return arr;
    }
}
