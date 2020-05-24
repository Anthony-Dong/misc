# Misc

[![](https://img.shields.io/badge/GitHub-1+-blue.svg?style=social&logo=github)](https://github.com/Anthony-Dong/netty-IM)[![](https://img.shields.io/badge/download-10-brightgreen.svg)](https://github.com/Anthony-Dong/netty-IM)![](https://img.shields.io/badge/language-java-green.svg)![](https://img.shields.io/badge/framework-netty-green.svg)

[TOC]

## 要求

- 要求JDK版本高于1.8
- 要求Netty 版本高于4.0
- 目前注册中心只集成了Zookeeper，Docker装一个zk就可以了，有脚本



## 特点

- misc-core 封装了 Netty的抽象层，将netty分为了 协议处理层、协议转换层、业务层。
- 目前协议支持HTTP、MISC自定义协议两种，文件协议还在开发，请看我的另一个分支
- misc-rpc 目前支持异步、同步调用，根据需求进行设置
- 高性能、高拓展
- misc协议，解码采用了五种序列化方式，MessagPack、Byte、JSON、Java、Hession2



## 快速开始 

服务接口: 

> ​	为了类型适应度测试

```java
public interface EchoService {
    int[] hashCodes(int _int, String _string, List<Integer> list);
}
```

服务端代码 : 

> zk 默认loaclhost:2181 , 服务端随机端口号

```java
@Test
public void runServer() throws Throwable {
    RpcServerConfig config = new RpcServerConfig();
    config.addInvoker(EchoService.class, (EchoService) (_int, _string, list) -> new int[]{_int, _string.hashCode(), list.hashCode()});
    MiscRpcServer.runSync(new ZKRegistryService(), config);
}
```

客户端代码: 

```java
@Test
public void runClient() {
    EchoService echoService = new ReferenceBean<>(EchoService.class, new ZKRegistryService()).get();
    int[] hash = echoService.hashCodes(1, "Hello Misc!", Collections.singletonList(1));
    System.out.printf("rpc invoke success , hash=%s", Arrays.toString(hash));
}
```

服务端输出 日志信息如下: 

```java
rpc invoke success , hash=[1, -871958617, 32]
```

这就是 一个最简单的一个Rpc例子. 

其他可以参考我的    ： [Demo](https://github.com/Anthony-Dong/misc/tree/master/misc-rpc/src/test/test/com/misc/rpc)

## 框架设计原则

### netty 三层处理器

```java
/**
 * 协议编解码器（不一定共享根据需求）
 */
private NettyCodecProvider<ProtoInBound, ProtoOutBound> codecProvider;
/**
 * 协议-业务转换器（共享）
 */
private NettyConvertHandler<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> nettyConvertHandler;

/**
 * 业务处理器（共享）
 */
private NettyEventListener<ChannelInBound, ChannelOutBound> nettyEventListener;
```

基于以上三层很好的进行各个层面的处理，协议编解码器很好的处理了请求协议的转换，比如http、misc等，而我们的协议-业务转换器核心是为了将协议和业务抽离出来，比如http->rpc请求封装，就是这个过程，业务处理器就是我们需要关注的地方。

### NettyCodecProvider

```java
public interface NettyCodecProvider<ProtoInBound, ProtoOutBound> {
    ChannelHandler[] get();
}
```

### NettyConvertHandler

```java
@ChannelHandler.Sharable
public abstract class NettyConvertHandler<ProtoInBound, ProtoOutBound, ChannelInBound, ChannelOutBound> extends ChannelDuplexHandler {
    protected abstract ChannelInBound decode(ProtoInBound msg) throws ConvertException;
    protected abstract ProtoOutBound encode(ByteBufAllocator allocator, ChannelOutBound msg) throws ConvertException;
}
```

### NettyEventListener

```java
public interface NettyEventListener<ChannelInBound, ChannelOutBound> {
    void connected(Channel channel) throws HandlerException;
    void disconnected(Channel channel) throws HandlerException;
    void sent(Channel channel, ChannelOutBound message) throws HandlerException;
    void received(Channel channel, ChannelInBound message) throws HandlerException;
    void caught(Channel channel, Throwable exception) throws HandlerException;
    default void eventTriggered(Channel channel, Object event) throws HandlerException {
    }
}
```



## Misc协议

### MiscPack数据包

> ​	基本就是下面三部分组成, 类似于HTTP的设计, router可以理解为头部信息, body可以理解为请求体/响应体 , 还有一个时间搓. 

```java
private String router; // 路由信息 (url信息,所以可以包含大多数的有规则的信息)

private byte[] body; // 数据体 (真正的数据体,比如:参数内容,消息内容等,同时用户可以压缩数据和加密)

private long timestamp; // 时间搓 (消息发送时间)
```

### 协议格式

协议格式 : 

`魔数(一个字节,固定为0XF) `   + `version (版本号 2个字节)`  + `type 协议类型(一个字节)`  

协议类型会进行判断分法给真正处理的协议.  比如message-pack , json , java序列化哇. 都很好地可以处理. 

比如如普通的JSON协议 . 

会有 `len (4个字节)`+ `body(len)` 组成.  我们拿到body , 会帮助我们反序列化成我们需要的对象(也就是Napck对象).

同时我们也引入了Message-Pack来序列化Java对象, 它利用哈弗曼树的优点进行压缩, 相比于JSON,更为轻量级. 所以很好地解决了文件大的问题.  

### 文件传输协议

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

### 半包/粘包/拆包等问题解决

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

## 文件传输协议（文件分支）

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

## 设计模式

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

## 注册中心/配置中心

基于Zookeeper ，也可以使用redis



## 下一步

### 完善功能

### 异步支持

### 开发MQ

### Go对接



## 如何贡献

- 拉取master，新建branch进行开发，最后提交找我合并
- 联系邮箱：574986060@qq.com