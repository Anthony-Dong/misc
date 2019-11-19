# netty-IM

> ​	还没有写完安卓客户端 , 希望写完就不采用 **HTTP长轮询** 方式了 , 直接点对服务器 ,纯TCP方式进行交互

## [IM的客户端](./chat-framework/chat-client) 

目录结构 都是 原生netty和spring是分开的 ,spring-boot启动 需要使用 `@EnableChatClient` 注解 启动

需要第三方的 redis 

## [IM的服务器端](./chat-framework/chat-server) 

目录结构 都是 原生netty和spring是分开的 ,spring-boot启动 需要使用 `@EnableChatServer` 注解 启动

需要第三方的 redis  和 zookeeper

## [IM的核心包](./chat-framework/chat-core) 

就是 一些netty的自定义编解码器 ,和一些监听器 , 以及一些工具包之类的 , client 和 server 依赖于这个

同时采用了 fastjson 和 messagepack 工具包


## [IM的配置中心](./chat-framework/chat-conf) 

需要第三方 zookeeper , 客户端连接不和 服务器直接打交道需要和这个玩意 , 他整合了spring-boot