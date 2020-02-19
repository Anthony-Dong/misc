# Netty- IM & RPC & MQ

[![](https://img.shields.io/badge/GitHub-1+-blue.svg?style=social&logo=github)](https://github.com/Anthony-Dong/netty-IM)[![](https://img.shields.io/badge/download-10-brightgreen.svg)](https://github.com/Anthony-Dong/netty-IM)![](https://img.shields.io/badge/language-java-green.svg)![](https://img.shields.io/badge/framework-netty-green.svg)


>  ​		支持SpringBoot快速启动 , 同时支持原生的Java代码开发,依赖很少, 核心三块:一个是Server,一个Client 一个是Context.
>
>  ​		要求JDK版本高于1.8 ,因为好多lambda表达式,希望谅解 ,是基于Netty实现的一个RPC调用 , 消息同步发送(ACK机制) , 文件传输服务(可以插入压缩等实现), 日志传输服务(日志远程处理和收集).   
>
>  ​		整体的设计架构很不错. Server端可以根据需求实现可插拔式的协议处理.  Client端可以根据服务端的协议进行发送.
>
>  ​        基于TCP传输层协议编程. 就会很方便的实现 , 如何压缩数据包体积, 如何进行高效的传输, 直传方式带来的优点 , 
>
>  ​		最后点赞一下Netty这个伟大的框架. 其实对于一个RPC,MQ,LOG传输之类的框架, 更多的体现在你框架的设计上, Netty只是帮助你解决了传输层问题 , 但是对于RPC框架, 如何做到高吞吐量,高效的序列化与反序列化, 高的可靠性 (目前测试没有消息丢失的情况,如果超时,会提供接口进行处理) ,  所以可靠性是有的, 吞吐量上,测试也很不多.  ACK机制性能很高.  可插拔式的协议处理, 使得编程变得更加方便 , 灵活 .
>
>  ​		如果你有兴趣可以看看Dubbo之类框架的整体设计架构. 如何分层次的解决问题. 



## PRC协议

> ​	默认响应超时时间可以用 : `client.timeout`系统属性配置. 等后期加入注解开发. 超时会抛出`TimeOutException`异常

服务端代码 : 

```java
public static void main(String[] args) throws Exception {
    // 需要暴露的接口, 和接口实现类. 
    RpcMapBuilder.addService(EchoService.class, new EchoService() {
        @Override
        public String echo() {
            return "hello world";
        }
        @Override
        public Map<String, Object> echo(Map<String, Object> msg, List<String> list) {
            System.out.printf("%s\t%s\n", msg.getClass(), list.getClass());
            msg.put("list", list);
            return msg;
        }
    });

    // 上下文对象
    ChatServerContext context = new DefaultChatServerContext();
    // 线程池 . 和dubbo默认的一致 200个线程池
    context.setThreadPool(new ThreadPool(200, -1, "work"));
    ChatServer.run(9999, context);
}
```

客户端代码: 

```java
public static void main(String[] args) throws Exception {
    // 上下文
    DefaultChatClientContext clientContext = new DefaultChatClientContext();
    // 注入上下文启动,默认是单个线程处理, 客户端,并不需要多个线程,编解码一个业务一个足矣
    AsyncChatClient client = AsyncChatClient.run(9999, clientContext);
    // 拿到一个代理对象, 需要告诉代理接口和上下文
    EchoService service = RpcProxy.newInstance(EchoService.class, clientContext);
    // rpc调用
    Map<String, Object> echo2 = service.echo(Collections.singletonMap("name", "a"), Collections.singletonList("value"));
    System.out.println(echo2);
    // 关闭客户端, 释放资源.
    client.close();
}
```

输出 : 

```java
// 服务器端,第一次处理响应会稍微慢一点.后续基本是0ms左右,还要看用户具体实现.
2020-02-19 12:17:27,468 7957   [er-3-1] DEBUG il.ResourceLeakDetectorFactory  - Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@566ec476
class java.util.HashMap	class java.util.ArrayList
2020-02-19 12:17:27,687 8176   [read-1] DEBUG lthandler.RecordRequestHandler  - [服务器] HandlerRequest Protocol:rpc , Url:rpc:///com.chat.core.inter.EchoService?id=1&method=echo.java.util.Map.java.util.List&timeout=1000 , Spend:360ms.
2020-02-19 12:17:27,749 8238   [er-3-1] INFO  HandlerRemovedChatEventHandler  - [服务器] Remove Client : /192.168.28.1:5936 .


// 客户端
2020-02-19 12:17:27,733 1905   [read-1] DEBUG der.ClientReadChatEventHandler  - [客户端] ReceiveResponse : rpc://0.0.0.0:9999?id=1.
{name=a, list=["value"]}
2020-02-19 12:17:27,733 1905   [  main] ERROR ClientShutDownChatEventHandler  - [客户端] 关闭成功 Host:0.0.0.0 Port:9999. 
```

这就是 一个最简单的例子. 



## MSG协议

> ​	默认响应超时时间可以用 : `client.timeout`系统属性配置. 等后期加入注解开发.

> ​	有些时候我们需要确认消息的ACK机制, 就是必须已经对方收到了, 此时就需要使用这个.  同时提供了不需要ack机制的.

```java
// 消息, 消息发送方, 消息接收方.
Response response = clientContext.sendMessageBySync("hello world", "tom", "tony");
System.out.println(response.getUrl());//需要ack
```

```java
clientContext.sendMessage("hello world", "tom", "tony"); // 不需要ack
```

日志信息 : 

```java
// 服务端:
2020-02-19 13:43:50,903 9789   [read-1] DEBUG lthandler.RecordRequestHandler  - [服务器] HandlerRequest Protocol:msg , Url:msg://?ack=1&id=1&receiver=tony&sender=tom&timeout=2000 , Spend:297ms.
2020-02-19 13:43:50,997 9883   [er-3-1] INFO  HandlerRemovedChatEventHandler  - [服务器] Remove Client : /192.168.28.1:6763 .
    
// 客户端:
msg://0.0.0.0:9999?id=1
2020-02-19 13:43:50,965 2202   [read-1] DEBUG der.ClientReadChatEventHandler  - [客户端] ReceiveResponse : msg://0.0.0.0:9999?id=1.   
```



## file / log协议

> ​	文件协议, 由于我们数据包设计的灵活, 可以处理很多类型 , 都是用户可以拓展的. 比如什么MQ哇, 都可以自己拓展实现. 

## 框架设计原则

#### Npack数据包

> ​	基本就是下面三部分组成, 类似于HTTP的设计, router可以理解为头部信息, body可以理解为请求体/响应体 , 还有一个时间搓. 

```java
private String router; // 路由信息 (url信息,所以可以包含大多数的有规则的信息)

private byte[] body; // 数据体 (真正的数据体,比如:参数内容,消息内容等,同时用户可以压缩数据和加密)

private long timestamp; // 时间搓 (消息发送时间)
```

#### 传输协议

我们的协议比较简单, 用户呢可以修改 , 在` com.chat.core.netty.PackageEncoder` 和 `com.chat.core.netty.PackageDecoder`   和这个包下面. 下面说说我们的实现吧. 

每一个完整的数据包 : 

version(2个字节)+length(4个字节)+napck(数据包)  就这四部分组成,本来还要加入校验码之类的信息, 但是大多数情况下是不会出现 version len npack解析会出现成功的问题, 这种概率极低 . 

version : 2个字节, 要求客户端服务器端 version版本一致, 才可以解码成功.

len : 4个字节, 指的是npack的体积, `我认为大部分情况下长度是不会超过2^31-1的大小`, 就算是文件协议也是不会去传输这么大的文件, 也会进行拆包处理. 

npack数据包 : 这个会涉及到序列化与反序列化问题, 结合我们的数据包固定设计, 我们采用了`MessagePack` 进行序列化, 效率很好, 相比于Hessian2和Java的序列化 , 结合了两者的优点, 他基本都媲美 (这个仅限于我们遵循MessagePack的对象,普通Java对象不可以,具体可以去[官网](https://msgpack.org/)看看).  也许有些人会说使用JSON来处理,然后转成字节流. 但是我觉得么必要. (body部分可能涉及到JSON来传输).



#### 拆包和粘包问题解决

对于Netty来说 , 他是不会帮助你进行拆包的, 你可能拿到的是多个对象, 也就是说, 可能一次拿到的是好几个数据包, 但是经过我测试发现, 他可以保证他的完整性. 对于大多数人开发一般都是使用`io.netty.handler.codec.ByteToMessageDecoder` 和 `io.netty.handler.codec.MessageToByteEncoder` 进行解码和编码的. 



编码其实很简单, 因为就是一个pack -> 一个网络数据包 , 只要格式遵循自己的协议就可以了 . 

解码其实最麻烦的. 第一粘包如何处理, 所以需要校验码. 我们就是len和version简单的校验. 我们对于数据依旧是靠我们的Npack对象进行后期处理.  

比如我们对于拿进来的`io.netty.buffer.ByteBuf` 对象,  需要实现此接口信息. 

```java
protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception;
```

out就是我们需要拿到的对象 , in->out的转变

首先判断是否可读(w指针>r指针). 可读才执行下一步.  -> 记录r指针位置, 后期可能要求重置  ->  读取两个字节的version(与我们的版本号对应一致,继续执行) -> 读取四个字节的len信息 -> 然后读取len长度的字节数组 -> 使用MessagePack反序列化  // 如何失败了, 代表此条bytebuf数据有问题,我们就重置r指针, 直接返回. //  如果成功了, 我们就将他存入到out中, 继续判断是否可读,重复执行. 就可以了. (Dubbo框架也是,不过他使用的是Multiple对象,这样也好其实. 不使用多个对象.)

基本就是上诉的问题了,  Netty对于编解码的实现机制 帮助开发者可以更加的面向需求编程, 而不是底层数据处理. 

#### 设计模式

对于事件处理方便采用了`监听者模式` , 使用回调的方式实现同步非阻塞编程. 

对于事件管理和分发 , 我们采用了`策略模式`(使用HashMap) 进行事件分发 , 比如注册事件, 会分发给RegisterHandler. 之类的很多. 

对于可插拔式的协议处理, 我们使用了 `责任链模式` , 比如我想要日志服务和RPC服务, 我们可以之加入这俩服务, 类似于Netty的Pipeline . 位置顺序可以改. 我们是依靠链表来实现的. 

还有更多的体现不到的 , 比如说`动态代理` , 也就是`代理模式 ` ,   RPC需要使用到大量的代理. 

`单例模式`  , 由于很多上下文信息哇, Handler都是单例的, 线程安全的对象. 

`工厂方法模式` 和`建造者模式` ,对于注册中心的实现, 就是Factory实现.  还有很多都是建造者模式. 

​		设计模式其实名字就是一个定义罢了. 桥接模式,比如我们的链接Netty的事件和我们真正的Handler,使用Map根据类型进行桥接, 这不就是桥接模式吗. 更多的体现在原则上. 



## 客户端设计

### 面向接口编程

对与客户端和服务器来说, 都是传输层接口, 多以都需要有的操做是 : 所以这就是接口要求.

```java
public interface ServerNode {
void init();
void start();
void shutDown();
}
```

EventListener机制 :

`com.chat.core.listener.ChatEvent` 接口 :  表示事件源 , 不使用Java原生接口是还要涉及到另一层包装, 是一种得不偿失的问题, 所以我们自己定义了Event , `ChatEventType` 是枚举类型 ,安全效率高.

```java
public interface ChatEvent {
    ChatEventType eventType();
    default Object event() {
        return NULL;
    }
}
```

事件监听者 : `com.chat.core.listener.ChatEventListener`  监听`ChatEvent` 事件. 这里继承了Java的原生接口.

```java
public interface ChatEventListener extends EventListener {
    void onChatEvent(ChatEvent event) throws Exception;
}
```

事件真正的处理者 : `ChatEventHandler` 

```java
public interface ChatEventHandler {
    void handler(ChatEvent event) throws HandlerException;
}
```

所以对于 Netty的每一个事件, 我们都申明了大量的 `ChatEventType` 枚举类型.  然后封装成我们事件类型, 告诉监听者. 监听者拿到后, 根据类型再选择真正的处理者.   很好地解耦. 

### 细节点: 

#### 一 . 拿到channel

​		`channel().closeFuture().sync()` 这段代码是一个阻塞的, 会一直等待客户端失败/或者异常 .  所以客户端不能阻塞, 因为要实现API操作. 所以不能用. 

​		Netty客户端如何与服务端发送消息呢, 我自己测试 ,发现依靠`channelActive` 事件返回的`ChannelHandlerContext` 上下文对象更加的符合逻辑. 因为我们并没有去做注册的测试应答机制, 前期没有考虑, 所以当真正注册成功后, 返回的这个上下文一定是可靠的.  

​		如何保证拿到这个对象, 又可以防止不出现问题, 我们引入了countdownlatch . 一个控制器, 只有拿到`ChannelHandlerContext`这个对象, 客户端才会真正初始化完成 (可以设置超时时间.)

####  二. 消息协议处理封装

基于URL类 , 可以高效的实现各种信息封装, `com.chat.core.model.URL` . 提供了很多构造URL的方法. 和提取. 并不受到限制 . 

#### 三. RPC响应/AKC机制

对于RPC来说无非也是ACK机制, 如何拿到结果, 这个是很关键的问题 , 是不是, 你发给别人了没回话, 那叫啥UDP吧, 不可靠, 你都不知道拿到没有. 

对于Java来说 , 可以参考 `java.util.concurrent.FutureTask` 的实现. 其实是一回事, 为啥Runnable不是一个带有返回值的方法呢.  这就是个问题, 所以引入了 `java.util.concurrent.Callable` 接口, 你调用runnable接口的时候调用了`java.util.concurrent.Callable` 接口, 拿到这个接口返回值, 告诉你不就得了. 

`FutureTask`是靠 volatile(状态量) 实现的, 你调用get方法, 会一直等待状态量发生改变, 也就是说等待callable接口返回值拿到.  所以基本上RPC/ACK都是这个道理.  需要不断的get() . 不使用回调注定是一个阻塞过程.  我感觉不符合大部分人的编程习惯. 

好比下面这样子, 没有返回结果, 依靠回调实现非阻塞编程 . 

```java
context.sendMessage("hello world", "tom", "tony",new Consumer<Response>(){
	// 等待回调结果
});
```

那么同步执行的代码是啥呢 ? 

```java
Response response = context.sendMessageBySync("hello world", "tom", "tony");
```

会等待拿到结果, 才继续往下执行. 



**我相信如果你看了上诉我说的, 非阻塞式编程实现很简单, 你应该会. 我这里不讲. 因为就是个回调,** 

阻塞如何做的 ? 

由于情况比较简单,  我们基本上是靠: ` Condition+Lock` 或者 Java的`wait`和`notify` 便可以实现 : 

文章可以看看我的[这篇文章](https://anthony-dong.gitee.io/post/JmBil-MQE/) : [https://anthony-dong.gitee.io/post/JmBil-MQE/](https://anthony-dong.gitee.io/post/JmBil-MQE/)  . 相信你一定会.  (重要的东西一定要自己明白才可以, 所以我不说, 可以看看我的实现: `com.chat.client.future.NpackFuture`)

如何区分你收到的响应和你发布的请求联系起来呢, 而且是多线程环境, 我使用的是 `ConcurrentHashMap`  , 那么这个ID如何做呢, 我第一次考虑的是时间搓(Java只支持ms类型, 不支持ns(`golang真香`))  ,精确度不够, pass掉. 第二次雪花算法, 单机没必要, 性能差,  第三次想到UUID,不行哇,太大了, 占用空间, 第三次LongAddr , 虽然保证了自增, 但是我们拿到的数据时候是可能一致的 , 最后就是 AtomicInteger/AtomicLong , 这俩基本可以满足. 对于客户端我觉得AtomicInteger足够了, 而且你服务要跑一辈子哇, 2^31-1 足够了. 如果不够可以换long类型. 



#### 四. 保证消息不丢失,可靠性高

RPC调用, 你可以保证客户端代码, 也就是服务消费方, 那么提供方,你可以告诉他超时吗, 不可以, 所以对于这个你发出了在数据不丢失的情况下 (不考虑限流) 一定会被调用,  那么有个问题 ? 请求超时, 但是服务端执行了, 对于那些幂等性操作, 执行N次结果不一样. 所以我们做了一种乐观处理的态度.  

> ​	如果超时 ? 我们去判断response是否为空 ? , 空我们就将Map中我们这条记录删除 . 那么客户端的响应结果拿到的时候会发现Map中没有这条数据 , 此时就调用fallback接口 , 用户可以提供. 保证不丢失.

## 服务端设计

​		基本上和客户端一样, 我主要讲讲拓展机制.  如果对于这种模式感兴趣我觉得可以看看Sentinel的实现. 他的设计原则上我基本和他一样. 对, 我借鉴了他 (实际上, 说抄罢了,哈哈哈哈) , 代码不就是互相参考吗. 

[我的文章写过Sentinel相关的内容](https://anthony-dong.gitee.io/post/YqSHXPtCX/) : [https://anthony-dong.gitee.io/post/YqSHXPtCX/](https://anthony-dong.gitee.io/post/YqSHXPtCX/)

Sentinel的官网呢 :  [https://github.com/alibaba/Sentinel/wiki/Sentinel工作主流程](https://github.com/alibaba/Sentinel/wiki/Sentinel工作主流程) 



SPI机制其实是一个类似于IOC的一种机制, 控制翻转. 用户只要根据约定就会给你实例化bean. Dubbo的SPI机制如果啥时候独立出来也很棒 ,他实现了另一套SPI机制. 



我们来看看这种模式的好坏, 第一SPI加载  : `RequestHandlerProcess` 这个处理器. 如果用户没有实现这个接口, 那么会默认使用系统提供的接口 . 一般框架开发人员提供的默认实现.  

```java
public class DefaultHandlerChainBuilder implements HandlerChainBuilder {
    @Override
    public RequestHandlerProcess build() {
        RequestHandlerProcess process = new RequestHandlerProcess();
        // 记录器, 花费多少时间哇处理的哇 ,打印日志(类似于环绕通知)
        process.addLast(new RecordRequestHandler());
        // 日志协议处理
//        process.addLast(new LogRequestHandler());
        // 消息协议处理
        process.addLast(new MessageRequestHandler());
        // 文件
//        process.addLast(new FileRequestHandler());
        // RPC协议处理
        process.addLast(new RpcRequestHandler(RpcMapBuilder.map));
        // 心跳处理.
        process.addLast(new HeartRequestHandler());
        return process;
    }
}
```

这就是我的这个实现.  用户如果想使用需要在 `META-INF/services` 创建一个文件名为`com.chat.server.spi.handler.HandlerChainBuilder` 这个的文件, 放入你实现接口的全限定类名. 

他会将他处理不了的不断的向下委派, 根据责任链来控制流程. (就是个链表结构) 我们的`com.chat.server.spi.handler.AbstractRequestHandler` 不断的委派. 

```java
@Override
public abstract void handler(Request request, ChannelHandlerContext context) throws HandlerException;
protected final void fireHandler(Request request, ChannelHandlerContext context) throws HandlerException {
    if (next != null) {
        next.handler(request, context);
    }
}
```

如何加载进去的 : 

```java
// 过滤器 SPI加载
this.filter = SPIUtil.loadFirstInstanceOrDefault(Filter.class, DefaultFilter.class);
// HandlerChainBuilder  SPI加载
HandlerChainBuilder builder = SPIUtil.loadFirstInstanceOrDefault(HandlerChainBuilder.class, DefaultHandlerChainBuilder.class);


//处理逻辑就在这里, 判断不为空处理
Objects.requireNonNull(process.getFirst()).handler(request, channelContext);
```

简单使用: `RecordRequestHandler`

```java
public class RecordRequestHandler extends AbstractRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RecordRequestHandler.class);

    @Override
    public void handler(Request request, ChannelHandlerContext context) throws HandlerException {
        // 先让别人处理完.
        try {
            fireHandler(request, context);
        } finally {
            // 最后统计时间.
            if (!request.getProtocol().equals(UrlConstants.HEART_PROTOCOL)) {
                logger.debug("[服务器] HandlerRequest Protocol:{} , Url:{} , Spend:{}ms.", request.getProtocol(), request.getUrl(), System.currentTimeMillis() - request.getTimestamp());
            }
        }
    }
}
```





## 学习方法

- **自上而下兴趣法** :  只有看到手机 , 不断的迷恋才会去学习要不我也设计一款手机, 然后硬件大佬诞生了. 这就是兴趣, 好奇是我们最大的成本 . 啥东西都是.   

- **自下而上学习法** :  
  - 框架学习三部曲 :
  - 第一步Github开源项目 down下来,   
  - 第二步 : 跑一遍Demo, 好的框架都有测试用例. 
  - 第三步 : 看看作者写的文档  , wiki哇, 其他之类的.
  - 最后就是, 小伙伴们喜欢的一个注解完事了, 学他做啥, 一个SpringBootApplication注解就启动了, 麻烦.



## 注册中心/配置中心

为了减少数据的依赖程度,前期选择的Zookeeper,后来考虑减少依赖因此选择了Redis. 对数据进行负载均衡操作.

用户可以自行拓展. 我感觉 redis 和 zookeeper 各有优缺点. 看大家如何做吧. 

可以参考Dubbo的实现. 其实Dubbo就是 可以拓展zookeeper 和 redis的. 





