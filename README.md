# Netty-IM

[![](https://img.shields.io/badge/GitHub-1+-blue.svg?style=social&logo=github)](https://github.com/Anthony-Dong/netty-IM)[![](https://img.shields.io/badge/download-10-brightgreen.svg)](https://github.com/Anthony-Dong/netty-IM)![](https://img.shields.io/badge/language-java-green.svg)![](https://img.shields.io/badge/framework-netty-green.svg)

测试玩玩

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]


>  支持SpringBoot快速启动 , 同时支持原生的Java代码开发 ,核心类两个,一个是Server,一个Client

## 快速使用

其业务核心是脱离启动代码的,客户端是可以通过上下文进行通信,服务器可以通过上下文保存客户端信息,还有通过SPI方式进行接口拓展

服务器端代码 : 

```java
ChatServerContext context = new ChatServerContext("server-1", (short) 1) {
  // 上下文名称和服务版本号
};
// 启动 , 后面线程阻塞
ChatServer.run(9999, context);
```

客户端快速启动

```java
ChatClientContext context = new ChatClientContext("app-1", (short) 1) {
   // 上下文名称和服务版本号
};
// 启动,后面线程阻塞
ChatClient.run(9999, context);
```

SpringBoot快速启动服务器

```java
@SpringBootApplication
@EnableChatServer
public class ChatServerApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
    }

    @Autowired
    private ChatServerContext context;

    @Autowired
    private ChatServerProperties properties;

    @Override
    public void run(String... args) throws Exception {
        ChatServer.run(properties.getPort(), context);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("关闭服务");
        }));
    }
}
```

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

基本已经实现了全部接口的编程

其中用户的拓展点在我的 `@SPI`  接口规范, 用户如果想覆盖默认实现需要标明 `@Primary` 注解, 主要接口就是`com.chat.client.spi.HandlerSenderPackage` 接口,用来处理服务器端发送来的消息的, 

同时他支持客户端同步流程实现,

```java
// 启动
AsyncChatClient client = AsyncChatClient.run(9999, context);

// 发送信息
context.sendPack(NPack.build());

// 关闭
client.close();
```

## Npack 数据包

> 传递数据的载体

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
NPack.buildWithByteBody("sender", "receive", "作业.docx", new File("..."))
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



## ChatHttpServer服务器

Http服务器

## ChatServerSpringBootStart

基于springboot自动化装配实现的 , 很好的解耦,有兴趣可以看卡, 希望可以加深你对于springboot自动装配的理解

## [IM的核心包](./chat-framework/chat-core) 

核心包,包含Netty中自定义编解码器之类的.有兴趣的可以看看.解决拆包粘包问题,

对于序列化采用的MessagePack框架,以最小的体积进行传输

同时采用了 fastjson 和 messagepack 工具包


## [IM的配置中心](./chat-framework/chat-conf) 

为了减少数据的依赖程度,前期选择的Zookeeper,后来考虑减少依赖因此选择了Redis. 对数据进行负载均衡操作.

[Build status]: 

[Build status]: 