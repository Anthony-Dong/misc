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

> ​	默认响应超时时间可以用 : `client.timeout`系统属性配置, 超时会抛出`TimeOutException`异常  , 为了对接网卡, 展示效果. 我的server端, 在Linux系统上, 也就是虚拟机上. 

服务接口: 

```java
public interface EchoService {
    Integer hash(String str);
}
```

服务端代码 : 

```java
public class ServerBoot {
	// 虚拟机IP是192.168.58.131
    public static void main(String[] args) throws Exception {
        // 1.暴漏的接口
        RpcMapBuilder.addService(EchoService.class, String::hashCode);
        // 2.上下文对象
        ChatServerContext context = new DefaultChatServerContext();
		// 3.设置序列号方式为 JSON(指的是响应的时候发送消息序列号为JSON),默认是MessagePack
        context.setSerializableType(SerializableType.JSON);
        // 4.设置线程池大小. 
        context.setThreadPool(new ThreadPool(20, -1, "work"));
        // 5.启动...阻塞会一直.
        ChatServer.run(9999, context);
    }
}
```

客户端代码: 

```java
public class ClientBoot {

    // 连接虚拟机
    public static void main(String[] args) throws Exception {
        // 1.初始化上下文
        DefaultChatClientContext clientContext = new DefaultChatClientContext();
        // 2.设置使用message_pack_zip
        clientContext.setSerializableType(SerializableType.MESSGAE_PACK_GZIP);
        // 3.启动
        ChatClient client = ChatClient.run("192.168.58.131",9999, clientContext);
        // 4.获取代理对象.
        EchoService service = RpcProxy.newInstance(EchoService.class, clientContext);
        IntStream.range(0, 10).forEach(value -> {
            // rpc调用
            Integer hash = service.hash("hello rpc");
            System.out.println(hash);
        });
        // 5.关闭客户端.释放资源.
        client.close();
    }
}
```

服务端输出 日志信息如下: 

```java
// 服务端日志信息
2020-02-02 13:13:36,337 1946   [  main] DEBUG er.ServerStartChatEventHandler  - [服务器] Start-up success host: 192.168.28.1, port: 9999, version:1, type: JSON, contextName:server-context, thread-size: 20, thread-queue-size: -1, thread-name: work.
2020-02-02 13:13:41,033 6642   [er-3-1] DEBUG nnelRegisteredChatEventHandler  - [服务器] Registered client address: /192.168.28.1:3584.
2020-02-02 13:13:41,549 7158   [read-1] DEBUG lthandler.RecordRequestHandler  - [服务器] HandlerRequest protocol: rpc, url: rpc://192.168.28.1:9999/com.chat.core.test.EchoService?id=1&method=hash.java.lang.String&timeout=111111111, spend: 444ms.
2020-02-02 13:13:41,583 7192   [read-2] DEBUG lthandler.RecordRequestHandler  - [服务器] HandlerRequest protocol: rpc, url: rpc://192.168.28.1:9999/com.chat.core.test.EchoService?id=2&method=hash.java.lang.String&timeout=111111111, spend: 1ms.
/// .... 记录了响应时间 . 同时记录日志
2020-02-02 13:13:41,618 7227   [er-3-1] INFO  HandlerRemovedChatEventHandler  - [服务器] Remove client address: /192.168.28.1:3584.    


// 客户端日志信息
2020-02-02 13:13:41,014 1804   [er-1-1] DEBUG der.ClientReadChatEventHandler  - [客户端] Connect server success host: 192.168.28.1, port: 9999, version:1, type: MESSGAE_PACK_GZIP, contextName:chat-server, thread-size: 1, thread-queue-size: 0, thread-name: Netty-Worker.
2020-02-02 13:13:41,581 2371   [read-1] DEBUG der.ClientReadChatEventHandler  - [客户端] ReceiveResponse : rpc://192.168.28.1:9999?id=1.
hash值 : 1195156887
2020-02-02 13:13:41,584 2374   [read-1] DEBUG der.ClientReadChatEventHandler  - [客户端] ReceiveResponse : rpc://192.168.28.1:9999?id=2.
hash值 : 1195156887
/// ... 还有很多 ,关闭日志
2020-02-02 13:13:41,614 2404   [  main] DEBUG m.chat.client.netty.ChatClient  - [客户端] Shutdown success the connected server host: 192.168.28.1, port: 9999.    
```

这就是 一个最简单的例子. 

## 消息应答协议

> ​	默认响应超时时间可以用 : `client.timeout`系统属性配置. 等后期加入注解开发.

> ​	有些时候我们需要确认消息的ACK机制, 就是必须已经对方收到了, 此时就需要使用这个.  同时提供了不需要ack机制的.

需要ACK

```java
// 消息, 消息发送方, 消息接收方.
Response response = clientContext.sendMessageBySync("hello world", "tom", "tony");
System.out.println(response.getUrl());//需要ack
```

不需要ACK

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



## 文件传输协议

最好不要本地测试, windows本地测试.比如同一个网卡下面是不会出入网卡的,所以速度很快,体现不出传输效果.

> ​	文件传输协议,由于文件比较大体积, 一般如果使用我们原来的协议的话, 会造成大量的数据拷贝. 所以我们采用的是新的一套文件协议,依靠Java的NIO实现的. 来看看传输效率吧先. 我们以20KB为每个包的大小进行发送, 同时他也可以混合和普通协议一起传输.. 

首先我们需要服务端开启文件传输协议 `context.setUseFileProtocol(true);` 这么就开启了. 

为了测试速度快慢, 我们使用一个1G左右的大文档进行传输. `962 MB (1,009,090,560 字节)` 的office2010.iso , 本地无法模拟速度的 , 因为windows测试的时候, 同一个网卡之间不会进行流入流出. 所以并不能体现速度. 

```java
public static void main(String[] args) throws Exception {
    DefaultChatClientContext clientContext = new DefaultChatClientContext();
    ChatClient client = ChatClient.run("192.168.58.131",9999, clientContext);
    long start = System.currentTimeMillis();
    // 发送一个 15.4 MB 的netty电子书 ,默认拆分大小为50K一个包.
    String s = clientContext.sendFileSync(new File("D:\\MyDesktop\\文档\\java\\Netty实战.pdf"));
    System.out.println(String.format("save path: %s , spend: %dms.", s, System.currentTimeMillis() - start));
    client.close();
}
```

输出 :  这是一个15M的文档, 跨网卡传输, 580MS. 速度还行. 

```java
save path: /home/admin/java-jar/file/Netty实战.pdf , spend: 580ms.
```

服务端 : 确实保存了. 

```java
[admin@hadoop1 file]$ ll
total 15872
-rw-rw-r-- 1 admin admin 16252675 Feb 28 14:04 Netty实战.pdf
```

为了增加难度. 我们测试了. 一个G的文档. 

```java
public static void test(DefaultChatClientContext clientContext) throws Exception {
    long start = System.currentTimeMillis();
    // 发送一个 15.4 MB 的netty电子书
    String s = clientContext.sendFileSync(new File("D:\樊浩东\软件\office2010.iso"));
    System.out.println(String.format("save path: %s , spend: %dms.", s, System.currentTimeMillis() - start));
}
```

测试的时候我不断 ll 命令的敲击  , 大约花了30S传完. 这个文档,模拟远程传输.  

```java
save path: /home/admin/java-jar/file/office2010.iso , spend: 29358ms.
```

查看虚拟机:

```java
[admin@hadoop1 file]$ ll
total 1001312
-rw-rw-r-- 1 admin admin   16252675 Feb 28 14:04 Netty实战.pdf
-rw-rw-r-- 1 admin admin 1009090560 Feb 28 14:10 office2010.iso
```

​	`为了保证协议互存的可靠性, 我们加入rpc协议进行验证.` 俩线程可以同时执行进行发送,服务端也可以很好的处理.  代码在本文档结尾. 一个线程执行发送1G文件, 另一个线程执RPC调用200

2001 次发送加存储文件没毛病. 效率还是可以的. 1G的文件从源拆包->发送->接收->拆包->落盘 同时还携带着2000其他包. 只需要4S多(本地测试,不跨网卡,所以很快), 还保证了不丢失的问题. 

## 框架设计原则

#### Npack数据包

> ​	基本就是下面三部分组成, 类似于HTTP的设计, router可以理解为头部信息, body可以理解为请求体/响应体 , 还有一个时间搓. 

```java
private String router; // 路由信息 (url信息,所以可以包含大多数的有规则的信息)

private byte[] body; // 数据体 (真正的数据体,比如:参数内容,消息内容等,同时用户可以压缩数据和加密)

private long timestamp; // 时间搓 (消息发送时间)
```

#### 普通传输协议

我们的协议比较简单, 用户呢可以拓展修改 , 在` com.chat.core.netty.PackageEncoder` 和 `com.chat.core.netty.PackageDecoder`   和这个包下面. 下面说说我们的实现吧. 

协议格式 : 

`魔数(一个字节,固定为0XF) `   + `version (版本号 2个字节)`  + `type 协议类型(一个字节)`  

协议类型会进行判断分法给真正处理的协议.  比如message-pack , json , java序列化哇. 都很好地可以处理. 

比如如普通的JSON协议 . 

会有 `len (4个字节)`+ `body(len)` 组成.  我们拿到body , 会帮助我们反序列化成我们需要的对象(也就是Napck对象).

同时我们也引入了Message-Pack来序列化Java对象, 它利用哈弗曼树的优点进行压缩, 相比于JSON,更为轻量级. 所以很好地解决了文件大的问题.  

#### 文件传输协议

协议头部, 依旧是上述讲的. 我们将文件协议类型分为开始写 + 写完, 所以就是两个类型. 

具体的实现逻辑在 `com.chat.core.netty.FileProtocol` 中实现. 

文件协议主要分. 

`魔数(一个字节)` + `服务版本号(两个字节)` + `协议类型(一个字节)` (start为127, end为126)+ `文件名长度(一个字节)` +`文件写入位置(8个字节)` +`文件写入具体长度(四个字节)`

以上部分就是协议头. 

依靠头部, 可以知道文件名和文件体 , 以及文件写入位置和长度. 就解决了Netty的NIO文件机制. (可以考虑传输的时候讲文件压缩.但是目前的压缩方式来说, 不支持之间内存压缩, 所以效果很差. 小文件还好.)

```java
// 写入
ByteBuf.writeBytes(in, position, length);

// 写出
ByteBuf.readBytes(out,position,length);
```

可以实现快速的IO . 

为了防止大量的实例化 stream 和channel. 我们使用map保存起来, 当发送end指令的时候我们会关闭. 或者当连接断开我们会释放资源.  对于Netty中只要是涉及到解码, 如果解码错误了,或者逻辑上发生错误了, 会有不可估计的后果, 就是OOM . 不断开连接的话. 

#### 半包/粘包/拆包等问题解决

​	对于Netty来说 , 他是不会帮助你进行拆包的, 你可能拿到的是多个对象, 也就是说, 可能一次拿到的是好几个数据包, 但是经过我测试发现, 他可以保证他的完整性. 对于大多数人开发一般都是使用`io.netty.handler.codec.ByteToMessageDecoder` 和 `io.netty.handler.codec.MessageToByteEncoder` 进行解码和编码的.  这个`ByteToMessageDecoder` 会帮助我们维护一个缓冲区, 每次没有读完的会帮助我们维护起来,所以对于半包问题我们基本可以解决.  同时可以调整 `discardAfterReads`大小来通过拷贝来防止OOM等问题.  **这里就要说Netty的不足之地了. 就是无法向一个ByteBuf中向前插入数据. 比如B1为 [R0,W100,C100], 我想将[R0,W5000,C5000]的数据插入到B1前面. 显然Netty的ByteBuf无法实现. 需要拷贝,其实对于Bytebuf来说, 底层是ByteBuffer, 再底层其实是数组, 数组是固定大小的, 添加一个也需要进行大量的复制.** 

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

## Netty如何优化和解决半包带来的数据拷贝问题

> ​	对于数据包大小如果我们可以精准的把控, 可以解决最少了复制次数. 解决问题.  那么就要找到入口在哪, 

​	对于Netty来说, 这点是可以做到的. 我们知道Netty是基于NIO实现的. 所以他接收数据的入口一定是`socketChannel.read() `方法.   源码在`io.netty.channel.nio.AbstractNioByteChannel.NioByteUnsafe#read`  方法中的 `doReadBytes(byteBuf)` , 其实进去就是一个需要实现 `RecvByteBufAllocator` 这个接口,提供一个Handler方法进行处理, 他类似于一个切面提供大小, 记录此时读取的大小.  所以可以自适应 . 

​	默认实现的是 :  `io.netty.channel.AdaptiveRecvByteBufAllocator`  , 这是一个自适应的, 默认可以到达64KB.最大值.   他同时也是一个自适应的可以根据记录进行调整大小. 会给一个初始值, 最小值, 最大值.(`但是必须是2的幂,比如20KB会取16KB的.所以这点注意`) . 

​		其实还有很多. 如果对于数据包大小是一定的情况下, 我们可以采用 `FixedRecvByteBufAllocator`  之类的. 一个合理的选择方式 可以为我们提供很好的性能提升. 

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

我们在处理的时候使用的是 : 

```java
// 下面就是事件处理器. 依靠map来维护事件处理器,这样拓展性很高.
final ChatServer server = new ChatServer(context.getProperties(), event -> {
            ChatEventHandler handler = handlerMap.get(event.eventType());
            handler.handler(event);
        }, context.getThreadPool());
```

### 细节点: 

#### 一 . 拿到channel

​		`channel().closeFuture().sync()` 这段代码是一个阻塞的, 会一直等待客户端失败/或者异常 .  所以客户端不能阻塞, 因为要实现API操作. 所以不能用. 

​		Netty客户端如何与服务端发送消息呢, 我自己测试 ,发现依靠`channelActive` 事件返回的`ChannelHandlerContext` 上下文对象更加的符合逻辑. 因为我们并没有去做注册的测试应答机制, 前期没有考虑, 所以当真正注册成功后, 返回的这个上下文一定是可靠的.  

​		如何保证拿到这个对象, 又可以防止不出现问题, 我们引入了countdownlatch . 一个控制器, 只有拿到`ChannelHandlerContext`这个对象, 客户端才会真正初始化完成 (可以设置超时时间.)

​		其实对于 Netty事件来说, 客户端连接真正完成的时候是在active事件触发, 同时 `ChannelFuture future = handler.connect(addr).sync();` 也会等待客户端完成连接之后才会响应结果. `sync`意思就是等待到连接成功建立. 

​		但是如果用这个`future.channel()` , 我们依靠是channel进行传输. 如果了解netty的话. channel传输是从低到上传输, 效率低, 其实效果并不好, 我们这里却是一种取巧的方式.

####  二. 消息协议处理封装

​		基于URL类 , 可以高效的实现各种信息封装, `com.chat.core.model.URL` . 提供了很多构造URL的方法. 和提取. 并不受到限制 .  所以我们不依靠与协议头进行封装信息. 我们主要依靠于我们的URL路径.

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

​	RPC调用, 你可以保证客户端代码, 也就是服务消费方, 那么提供方,你可以告诉他超时吗, 不可以, 所以对于这个你发出了在数据不丢失的情况下 (不考虑限流) 一定会被调用,  那么有个问题 ? 请求超时, 但是服务端执行了, 对于那些幂等性操作, 执行N次结果不一样. 所以我们做了一种乐观处理的态度.  

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



## 测试代码

文件传输+RPC测试.

```java
public static void main(String[] args) throws Exception {
    DefaultChatClientContext clientContext = new DefaultChatClientContext();
    AsyncChatClient client = AsyncChatClient.run(9999, clientContext);
    CountDownLatch latch = new CountDownLatch(2);
    long start = System.currentTimeMillis();
    new Thread(() -> {
        String response = clientContext.sendFileSync(new File("office2010.iso"), 1024 * 100);
        System.out.printf("文件存储路径 : %s\n", response);
        latch.countDown();
    }).start();

    EchoService echoService = RpcProxy.newInstance(EchoService.class, clientContext);
    new Thread(() -> {
        IntStream.range(0, 2000).forEach(value -> System.out.println(echoService.hash("hello file")));
        latch.countDown();
    }).start();

    // 等待.
    latch.await();
    System.out.printf("耗时 : %dms\n", System.currentTimeMillis() - start);
    client.close();
}
```





## 打包 - 发布-使用

我们加入了Maven脚本, 用idea , 直接将core包, (install明了)发布到自己的maven仓库, 然后打包server端 . 记住放入自己的主类, 在pom文件中设置. 然后package打包一下就可以; 直接java -jar 启动 , 具体参数设置



## 配合Golang开发 实现跨平台.

具体实现可以看[https://github.com/Anthony-Dong/golang-netty-rpc](https://github.com/Anthony-Dong/golang-netty-rpc )  , 我的这个实现. 

实现很简单Golang原生性能就很强, 主要是编解码机制的实现, 封装Bytebuf对象进行操作, 等. 基本还是很简单的. 

对接Java还是可以实现的. 

主要是在想如何实现接口话调用, 比如RestTemplate 一样 , Java的Rpc会保存接口信息. 一级参数类型, 等等. 都需要考虑到. 

但是Golang调用Java显得,有一些难度. 必须重新定义. 等我看看Dubbo-go的实现吧, 期待. 