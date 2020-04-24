package com.netty;



/**
 * TODO
 *
 * @date:2020/2/2 21:10
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Demo {

    public static void main(String[] args) throws Exception{


        System.out.println("test() = " + test());
    }


    public static int test(){
        int x = 1;
        try {
            throw new RuntimeException("hello world");
        } catch (Exception e) {
            System.out.println("exeception");
            return x;
        }finally {
            x = 3;
            System.out.println("finally");
            return x;
        }
    }
}
