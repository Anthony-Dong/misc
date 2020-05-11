package com.misc.core.model.netty;

import com.alibaba.fastjson.JSON;

/**
 * 参数对象 - > RPC调用时封装的信息, 这里考虑为什么我要转成JSON将每一个对象.
 * 第一如果使用接口上的类型, 作为反序列类型, 那么会出现很多类型和传入类型不一致问题. 参数是map,可是我传入hashmap,那么反序列成MAP的化,丢失了很多问题
 * 第二就是 内部类无法处理 , 所以很麻烦. 这点 . 我不知道如何处理
 *
 * @date:2020/2/17 16:54
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Arg {

    /**
     * 索引
     */
    private int index;

    /**
     * 类型
     */
    private Class<?> clazz;

    /**
     * 对象值,JSON值,省得服务器再转
     */
    private String value;


    @Override
    public String toString() {
        return "Arg{" +
                "index=" + index +
                ", clazz=" + clazz +
                ", value=" + value +
                '}';
    }

    public Arg() {
    }

    public Arg(int index, Class<?> clazz, String value) {
        this.index = index;
        this.clazz = clazz;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static Arg of(int index, Object object) {
        return new Arg(index, object.getClass(), JSON.toJSONString(object));
    }
}
