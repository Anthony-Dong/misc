# Netty-IM

[![](https://img.shields.io/badge/GitHub-1+-blue.svg?style=social&logo=github)](https://github.com/Anthony-Dong/netty-IM)[![](https://img.shields.io/badge/download-10-brightgreen.svg)](https://github.com/Anthony-Dong/netty-IM)![](https://img.shields.io/badge/language-java-green.svg)![](https://img.shields.io/badge/framework-netty-green.svg)


>  支持SpringBoot快速启动 , 同时支持原生的Java代码开发 ,核心类两个,一个是Server,一个Client 
>
>  要求JDK版本高于1.8 , 是一个Netty框架的炉子. 基本可以试下可拓展之类. 
>
>  可以支持RPC , 也可以支持IM . 其实这俩不矛盾(IM也是RPC,存取消息不就是调用远程save方法吗,区别在于服务器推送). 其实我觉得聊天如果不采用Http的方式, 无非需要更快的RT , 以及服务端更快的响应 . 还有就是自定义拆包. 还有可能设计到数据加密之类的. 还有就是体积小, 频率快. 
>
>  所以基于TCP编程. Netty很好的实现了. 



## 快速开始

服务端代码 : 

```java
public class ServerBoot {
    public static void main(String[] args) throws Exception {
        // 主线程阻塞
        ChatServer.run(9999, new ChatServerContext() {
            // 简单测试不需要重写任何方法.           
        });
    }
}
```

客户端代码: 

```java
public class ASyncClientBoot {

    public static void main(String[] args) throws Exception {
        // 1. 初始化一个Context
        ChatClientContext context = new ChatClientContext() {};
        // 2. 启动
        AsyncChatClient client = AsyncChatClient.run(9999, context);
        // 3. 发送
        context.sendPack(NPack.buildWithJsonBody("b", "a", Collections.singletonList("hello world")));
        // 4. 关闭客户端
        client.close();
    }
}
```



我们查看日志

服务端: 

```
2020-02-03 20:26:29,639 0      [  main] INFO  er.ServerStartChatEventHandler  - [服务器] 启动成功 Host:0.0.0.0 , Port:9999 , Version:1 , ContextName:default-chat-server-name.
2020-02-03 20:26:34,524 4885   [er-3-1] INFO  nnelRegisteredChatEventHandler  - [服务器] Registered Client : /192.168.28.1:11990 .
2020-02-03 20:26:34,927 5288   [read-1] INFO  .spi.DefaultSaveReceivePackage  - [服务器] Receive Pack : NPack[router={type=json&sender=b&receiver=a&classname=java.util.Collections$SingletonList}, timestamp=1580732794575].
2020-02-03 20:26:34,934 5295   [er-3-1] INFO  HandlerRemovedChatEventHandler  - [服务器] Remove Client : /192.168.28.1:11990 .
```

客户端 : 

```java
2020-02-03 20:26:33,517 0      [  main] INFO  t.client.netty.AsyncChatClient  - [客户端] 开始启动 Host : 0.0.0.0  Port : 9999 .
2020-02-03 20:26:34,504 987    [  main] ERROR er.ClientStartChatEventHandler  - [客户端] 启动成功 Host:0.0.0.0 Port:9999 Version : 1 ContextName :chat-server-name .
2020-02-03 20:26:34,504 987    [read-1] INFO  der.ClientReadChatEventHandler  - [客户端] 注册成功 , IP : 0.0.0.0/0.0.0.0:9999.
2020-02-03 20:26:34,579 1062   [  main] ERROR ClientShutDownChatEventHandler  - [客户端] 关闭成功 Host:0.0.0.0 Port:9999. 
```

这就是快速使用. 其他操作. 用户还可以自己拓展SPI接口和Context的方法重写. 

## SPI 机制

可以看看我这篇文章  https://anthony-dong.gitee.io/post/spi/  , 写了很多关于SPI的文章

其实SPI 是一种IOC的机制 , 控制翻转. 实例化对象不需要你来管理. 用户可以基于接口轻松的拓展实现, 以规定的方式注入进去就行了 . 

本框架中 , 主要是看我 `com.chat.core.annotation.SPI` 这个注解标记的接口. 

用户实现需要实现接口, 并且标记有 `com.chat.core.annotation.Primary` 才可以覆盖默认实现的. 

## [ChatClient的客户端](./chat-framework/chat-client) 

> ​	客户端

基本结构图 : 

```java
├─hander  // 事件处理器
│      ChatClientContext.java //客户端上下文,可以拿到context对象向server端发送消息
│      ClientChatHandlerConstant.java
│      ClientConnectedChatEventHandler.java
│      ClientReadChatEventHandler.java
│      ClientShutDownChatEventHandler.java
│      ClientStartChatEventHandler.java
│
├─netty // netty核心包
│      AsyncChatClient.java
│      ChantClientHandler.java
│      ChatClient.java
│      ChatClientChannelInitializer.java
│      ClientHeartBeatHandler.java
│
└─spi  // 核心接口
        DefaultHandlerSenderPackage.java
        HandlerSenderPackage.java
```

主要有两个 , 一个是`SyncChatClient` 他会阻塞当前线程,用户可以基于`com.chat.client.spi.HandlerSenderPackage`  拓展这个接口进行发送消息(这是基于回调实现的), 我们选择ChannelActive的ctx作为用户的Clinet的外部ctx,

用户如果想采用外部方式发送消息的话, 所以必须异步实现, 因此可以使用这个实现类`com.chat.client.netty.AsyncChatClient`  

## Npack 数据包

> 传递数据的载体 , 使用的是[MessagePack](https://msgpack.org/) 进行转换字节流. 因为网络传输(IO) 必须序列化反序列化. Java的序列化太占用体积了.  使用起来很简单 . 不用过多介绍. 

普通信息 : 

```java
NPack.buildWithStringBody("sender", "receive", "message")
```

Json信息

```java
NPack.buildWithJsonBody("sender", "receive",new Object())
```

传输文档

```java
NPack.buildWithByteBody("sender", "receive", "作业.docx", new File("a.jpg"))
```

释放对象:  主要是为了快速的GC , 减少对象引用. 

```java
pack.release();
```

## [ChatServer的服务器端](./chat-framework/chat-server) 

跟客户端是一样的, 也是SPI 拓展口, 其中是为了帮助用户进行对外拓展的,

其中用户的拓展点在我的 `@SPI`  接口规范, 用户自己实现需要标明 `@Primary` 注解, 来覆盖默认加载,

主要拓展接口 `com.chat.server.spi.Filter` 和 `com.chat.server.spi.SaveReceivePackage` 和这两个接口.

核心类 : 

```java
├─handler
│      ChatServerContext.java
│      ServerChannelRegisteredChatEventHandler.java
│      ServerChatHandlerConstant.java
│      ServerHandlerRemovedChatEventHandler.java
│      ServerReadChatEventHandler.java
│      ServerShutdownChatEventHandler.java
│      ServerStartChatEventHandler.java
├─netty
│      ChatServer.java
│      ChatServerHandler.java
│      ChatServerHeartBeatHandler.java
│      ChatServerInitializer.java
└─spi
        DefaultFilter.java
        DefaultSaveReceivePackage.java
        Filter.java
        HandlerReceivePackage.java
        SaveReceivePackage.java
```



**用户只需要拓展,俩接口 :** 

过滤接口 :  由于并没有采用链式过滤, 因为我觉得一个过滤器就可以了. 用户可以自行在过滤器里面链式其实也是可行的. 

```java
@SPI
public interface Filter {
    boolean doFilter(NPack pack) throws HandlerException;
}
```

保存接口 :  也是主要的处理逻辑 . 

```java
@SPI
public interface SaveReceivePackage {

    /**
     * @param pack NPack 数据包
     * @param context 当前连接的上下文. 可以用来发送消息之类的. 
     */
    void doSave(NPack pack, ChannelHandlerContext context) throws HandlerException;
}
```

用户可以基于Java的SPI 机制注入进去 , 记住对于用户实现的SPI , 必须设置`@Primary` 注解. 以覆盖原有的实现. 

理由就是链式处理不一定有单个处理好. 如果基于事件的话确实链式确实不错. 

## ChatHttpServer服务器

一个 Netty 实现的Http 服务器. 大家可以看一下 . 

## SpringBootStart

基于springboot自动化装配实现的 , 很好的解耦,有兴趣可以看卡, 希望可以加深你对于springboot自动装配的理解

## [Chat-Core](./chat-framework/chat-core) 

核心包,包含Netty中自定义编解码器之类的.有兴趣的可以看看.解决拆包粘包问题,

对于序列化采用的MessagePack框架,以最小的体积进行传输

同时采用了 fastjson(Jackson最好, fastJson好多特殊格式处理不了) 和 messagepack 工具包


## [IM的配置中心](./chat-framework/chat-conf) 

为了减少数据的依赖程度,前期选择的Zookeeper,后来考虑减少依赖因此选择了Redis. 对数据进行负载均衡操作.

用户可以自行拓展. 我感觉 redis 和 zookeeper 各有优缺点. 看大家如何做吧. 

可以参考Dubbo的实现. 其实Dubbo就是 可以拓展zookeeper 和 redis的. 