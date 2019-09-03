# rpc-ext
## 主要功能规划

|功能名|简要功能描述|功能进度|
|:-|:-|:-|
|DUBBO rpc服务应用级别点对点直连|在本地测试可以，通过配置参数，将指定应用下的所有rpc服务都指向指定的服务器|完成|
|DUBBO rpc服务服务端客户端统一配置|将dubbo的rpc服务的服务端配置信息和客户端配置信息通过自定义注解配置到api接口上|规划中|

## DUBBO rpc服务应用级别点对点直连

### 解决的痛点
由于现有的rpc服务框架的服务暴露和服务发现都是基于服务级别的，所以导致在对rpc的单元测试当需要点对点直连时，直连的粒度也是到服务级,一旦要测的功能设计到的rpc服务较多时，如果通过原始的点对点直连配置，就需要大量的配置来，加大了rpc服务的单元测试难度。

### 其他的解决方案
通过个性化配置文件，每个开发者通过指定的唯一的版本号将本地服务发布到注册中心，在服务引用时通过这个特定的版本好来寻找服务。但是该方案还是存在了在每个服务上都需要加上指定版本好的占位符配置项，而且是必不可少的，这样虽然解决了问题但还是会引来太多的额外的配置信息。

### 本方案的解题思路
  
  a.通过在api的根包下加上自定义注解，用来指定api包的服务的在集群中的唯一标示：应用名
  
  b.在客户端服务的启动参数里加上点对点直连配置项
  
  c.在spring容器初始化时根据点对点直连配置项修改指定应用下的referenceBean的定义信息，即加上直连的url地址和扩大rpc服务的超时事件为五分钟

### 使用方式

#### api包中定义RpcInfo的使用方式
1.在api根包下添加cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcInfo注解

如:服务端提供的api包的更目录为cn.hill4j.rpcext.demo.dubbo.api,则在cn.hill4j.rpcext.demo.dubbo.api添加package-info.java并在package-info.java添加RpcInfo并指明应用名“api-test”

![pgkinfoConfig](./readme/img/pgkinfoConfig.png "pgkinfoConfig")

package-info.java的内容
```
@RpcInfo(appName = "api-test")
package cn.hill4j.rpcext.demo.dubbo.api;

import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcInfo;
```

2.客户端启动参数添加应用直连地址配置项

应用直连地址配置项格式:

`rpc.reset.${appName}.url=dubbo://${ip}:${port}`

![clientTestConfig](./readme/img/clientTestConfig.png "clientTestConfig")


#### api包中未定义RpcInfo的使用方式一

1.客户端启动参数添加应用与包之间映射关系的配置项

格式：`rpc.reset.${appName}.apiPackage=${rootPackageName}`

2.添加应用直连地址配置项

格式：`rpc.reset.${appName}.url=dubbo://${ip}:${port}`

示例：

![noRpcInfoConfig1](./readme/img/noRpcInfoConfig1.png "noRpcInfoConfig1")


#### api包中未定义RpcInfo的使用方式二

1.客户端启动参数直接添加java包与直连地址之间映射关系的配置项

格式：`rpc.pkg.reset.${rootPackageName}.url=dubbo://${ip}:${port}`

示例：

![noRpcInfoConfig2](./readme/img/noRpcInfoConfig2.png "noRpcInfoConfig2")

