## 目录结构

```
example-common-------示例的公共结构
example-consumer-----服务消费者示例
example-provider-----服务提供者示例
h-rpc-base-----------hrpc框架的精简版
h-rpc-core-----------hrpc框架
	|-config
	|-constant
	|-model
	|-proxy
	|-registry
	|-serializer
	|-server
	|-spi
	|-utils
	|-
```



## h-rpc-base 架构图

![](./docs/images/h-rpc-base.png)

## 全局配置加载

`RpcConfig` 全局配置类记录 hrpc 框架的全部配置选项并赋予初始值，通过 `RpcApplication` 使用双重检查锁单例模式来维护全局配置对象实例 `RpcConfig`，使用时只需调用 `RpcApplication.init()` 即可读取 `application-xxx.properties` 文件里默认以 **hrpc** 开头的配置。

## 消费方调用 - 动态代理

使用 **JDK 动态代理** 来拦截消费方的方法调用，然后把调用转换成远程请求（HTTP2）。

代理的职责是抽象出远程调用的细节，使得客户端代码看起来像是在调用本地方法，而实际上这些方法是在远程服务器上执行的。代理类充当了中介角色，屏蔽了底层的网络通信和序列化/反序列化等细节。

## 基于 Vert.x 搭建的提供者服务端

- Vert.x 采用基于事件驱动的**非阻塞异步 I/O**模型，类似于 Node.js。
- 使用少量的**事件循环**线程（Event Loop），通过异步编程模型来处理大量并发请求，不需要为每个请求分配一个线程。
- 这种模型适合处理高并发、高吞吐量的应用，尤其是 I/O 密集型的场景。

## 自定义序列化器 (SPI 机制)

系统内置了 JDK、JSON、KRYO、HESSIAN 的序列化器。

开发者可以自定义自己的序列化器（实现 `Serializer` 接口），并在 META-INF/rpc/custom 目录创建 `org.howard.hrpc.serializer.Serializer` 文件，以 `key=自定义序列化器的全类名` 注册自定义的序列化器，使用时在 `application-xxx.properties` 文件里配置 `hrpc.serializer=key` （**服务消费者和服务提供者都需要配置**）即可使用自定义序列化器。

## 注册中心

> 存储结构设计

![](./docs/images/registration-center.png)

> 注册中心的行为

```java
public interface Registry {
    /**
     * 初始化
     *
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务，服务端
     *
     * @param serviceMetaInfo
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务，服务端
     *
     * @param serviceMetaInfo
     */
    void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 服务发现（获取服务节点列表），消费端
     *
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();

    /**
     * 心跳检测（服务端）
     */
    void heartbeat();

    /**
     * 监听服务（消费端）
     * @param serviceKey
     */
    void watch(String serviceKey);
}
```

> 调用流程

![](./docs/images/registration-center-call-process.png)

### 心跳检测和续期机制

心跳检测（俗称 heartBeat）是一种用于监测系统是否正常工作的机制。它通过定期发送心跳信号（请求）来检测目标系统的状态。如果接收方在一定时间内没有收到心跳信号或者未能正常响应请求，就会认为目标系统故障或不可用，从而触发相应的处理或告警机制。

从心跳检测的概念来看，实现心跳检测一般需要两个关键：**定时**、**网络请求**。

使用 etcd 实现心跳检测会更简单一些，因为 etcd 自带了 key 过期机制，不妨换个思路：利用服务注册的**续期机制**，给节点注册信息一个“生命倒计时”，让节点定期续期，重置自己的倒计时。如果节点已宕机，一直不续期，etcd 就会对 key 进行过期删除。

这样一来，利用服务注册的续期机制，既做到了定时，又完成了网络请求，既是续期又是心跳检测。

> 具体方案

1. 服务提供者向 etcd 注册自己的服务信息，并在注册时设置 TTL （生存时间）。etcd 在接收到服务提供者的注册信息后，会自动维护服务信息的 TTL，并在 TTL 过期时删除该服务信息。
2. 在注册中心初始化时开启心跳检测，心跳检测 `heartbeat()` 会定期检查当前节点所有的服务是否已经过期，如果没有过期则调用 `register()` 重新刷新过期时间（续期）。

### 服务节点下线机制

当服务提供者节点宕机时，应该从注册中心移除掉已注册的节点，否则会影响消费端调用，所以需要设计一套服务节点下线机制。

服务节点下线又分为：

- 主动下线：服务提供者项目正常退出时，主动从注册中心移除注册信息。
- 被动下线：服务提供者项目异常推出时，利用 etcd 的 key 过期机制自动移除。

利用 JVM 的 Shutdown Hook 就能实现。 JVM 的 Shutdown Hook 是 Java 虚拟机提供的一种机制，允许开发者在 JVM 即将关闭之前执行一些清理工作或其他必要的操作，在此删除当前节点在注册中心的服务信息即可。

### 消费端服务缓存

正常情况下，服务节点信息列表的更新频率是不高的，所以在服务消费者从注册中心获取到服务节点信息列表后，完全可以缓存在本地，下次就不用再请求注册中心获取了，能够提高性能。

使用缓存永远要考虑的一个问题就是数据一致性问题，此处也是，如果服务提供者节点下线了，如何让消费端及时知晓。这里也无需引入其他技术，利用 etcd 的监听机制。

监听机制允许 etcd 的客户端为一个键创建一个事件回调（PUT、DELETE），一旦这个键发生对应事件即可执行相应回调，在回调里面及时更新本地缓存。

![](./docs/images/etcd-watch.png)