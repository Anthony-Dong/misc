package com.chat.json;

/**
 * TODO
 *
 * @date:2020/2/18 22:13
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class TestBean {

    private TestBean() {
    }

//    private TestBean(String name, Integer age) {
//        this.name = name;
//        this.age = age;
//    }

    private String name;
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }

    public String getName() {
        return name;
    }

    private int age;

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public static TestBean of(String name,Integer age){
        TestBean bean = new TestBean();
        bean.age = age;
        bean.name = name;
        return bean;
    }
}
