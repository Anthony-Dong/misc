# netty-IM

> ​	还没有写完安卓客户端 , 希望写完就不采用 **HTTP长轮询** 方式了 , 直接点对服务器 ,纯TCP方式进行交互

## [IM的客户端](./chat-framework/chat-client) 

基本已经实现了全部接口的编程

其中用户的拓展点在我的 `@SPI`  接口规范, 用户自己实现需要标明 `@Primary` 注解, 来覆盖我的加载,

客户端主要的入口函数就是在于

`com.chat.client.netty.ChatClient` 类,启动可以直接使用

```java
 ChatClient.run(8888, ChatClientContext.NULL);
```

复杂使用

```java
public class ClientBoot {

    public static void main(String[] args) throws Exception {

        final ChatClientContext context = new ChatClientContext("app-1") {
            @Override
            protected void onStart() {
                // 连接成功
            }

            @Override
            protected void onFail() {
                // 连接失败
            }

            @Override
            protected void onReading(NPack context) {
                // 读请求
            }

        };

        new Thread(() -> {
            try {
                // 由于会阻塞当前线程, 所以我们开启新的线程, 看自己需求吧, 可以改我源码
                ChatClient.run(8888, context);
            } catch (Exception e) {
            }
        }).start();
        
        // 唯一写入口
        ChannelHandlerContext channelHandlerContext = context.getContext();

        // 测试异常
        testError(channelHandlerContext);

        // 测试文件上传
        testFileUpload(channelHandlerContext);

        // 测试字符串
        testString(channelHandlerContext);

        // 测试JSON使用
        testJson(channelHandlerContext);
    }
}

```



## [IM的服务器端](./chat-framework/chat-server) 

跟客户端是一样的,  也是SPI 拓展口, 其中暴露的接口也给用户了

其中用户的拓展点在我的 `@SPI`  接口规范, 用户自己实现需要标明 `@Primary` 注解, 来覆盖我的加载,

```java
public class ServerBoot {

    public static void main(String[] args) {
        ChatServerContext context = new ChatServerContext() {
            @Override
            public void onStart() {
                // 服务器启动
            }

            @Override
            public void onFail() {
               // 服务器关闭
            }

            @Override
            public void onRemove(ChannelHandlerContext context) {
                // 客户端移除
            }

            @Override
            public void onRegister(ChannelHandlerContext context) {
                // 客户端连接
            }
        };

        try {
            ChatServer.run(8888, context);
        } catch (Exception e) {
            //
        }
    }
}
```



主要拓展接口 `com.chat.server.spi.Filter` 和 `com.chat.server.spi.SaveReceivePackage`



## IM的HTTP端



## [IM的核心包](./chat-framework/chat-core) 

就是 一些netty的自定义编解码器 ,和一些监听器 , 以及一些工具包之类的 , client 和 server 依赖于这个

同时采用了 fastjson 和 messagepack 工具包


## [IM的配置中心](./chat-framework/chat-conf) 

还没有写, 等我改改 , 原来写的不好