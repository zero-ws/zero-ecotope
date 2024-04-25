# Zero Ecotope Framework

[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

Zero Ecotope Framework 是一个基于 [Vert.x](http://vertx.io) 的中间件容器，它可以帮助软件开发人员在 Vert.x
中快速开发和实施，集中精力处理项目需求中的业务逻辑而忽略开发过程中的部分细节，项目起源于早期在 Vert.x
生态中缺少类似 [Spring Boot](https://spring.io/projects/spring-boot/) 的快速开发工具，Zero 项目的目标是打造 Vert.x 生态中的
Spring Boot，目前整个框架已经发展到**第六个**年头，大大小小运行了15个左右的企业项目：

![](https://raw.githubusercontent.com/zero-ws/zero-ecotope/master/docs/_image/life.png)

> 一直没有发布 1.0 的版本是因为作为工业级和企业级项目框架，对数据本身比较严谨，一直等待着收集更多生产环境数据，从功能、性能、交付效率上为开发人员提供更加成熟的值得信赖的解决方案。

2023年，Zero前后端进行了大规模的重构，前端引入了 `微框架` 和 `开发中心`，后端引入了云原生对接（[K8S](https://kubernetes.io/)
和 [Istio](https://istio.io/) ）、行业建模工具、元数据标准、企业数字化辅助方案，对 **开发、测试、运维**
流程提供了更加成熟的功能，解决企业在数字化转型过程中面临的各种问题。

## 0. 引导

- （后端）Zero Ecotope：<https://www.zerows.io>
- （前端）Zero UI：<https://www.vertxui.cn>
- （工具）Zero AI：<https://www.vertxai.cn>
- （标准）Zero Schema：<https://www.vertx-cloud.cn>

## 1. 基础功能

* [功能支持表（英文）](FEATURES.md)

### 1.1. 项目结构

最新版的项目表（新版拆分在不同的库中）:

| Name           | Comment                                                                                  |
|----------------|------------------------------------------------------------------------------------------|
| zero-ecotope   | （语义：生态）<br/>根项目主项目、版本管理、文档管理等。                                                           |
| zero-epic      | （语义：史诗篇章）<br/>内含 AMS （Agreed Metadata Specification）的接口设计和定义规范，可作为最底层跨框架的基础功能库底座。        |
| zero-elite     | （语义：精英）<br/>Zero Core 核心框架，包含各种内置组件、功能函数、编排器等，为上层容器提供基础功能支撑。                             |
| zero-energy    | （语义：能源）<br/>Zero Web 容器，容器主要用于单机运行和OSGI插件化运行，OSGI部分开发中。                                  |
| zero-equip     | （语义：装备）<br/>**Infix Architecture** 下的功能插件模块。                                             |
| zero-extension | （语义：扩展）<br/>**Zero Extension** 扩展业务插件模块，类似 [ODOO](https://www.odoo.com/). 的功能拓展，带部分业务功能。 |
| zero-external  | （语义：外部集成）<br/>**Zero External** 动态建模专用外联插件。                                              |
| zero-entry     | （语义：入口）<br/>Zero入口项目，包含开发模式的 `import POM` 和单机版脚手架专用依赖集。                                  |

### 1.2. 常用链接

| 项                                                                      | 说明                                                                                                         |
|------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------|
| [Zero Ui](https://www.vertxui.cn)                                      | Zero Ui：基于 `React` 和 `Ant Design (Pro/g2/g6)` 的前端框架。                                                       |
| [Zero Ai](https://www.vertxai.cn)                                      | Zero Ai：快速开发常用脚本工具箱、代码生成、数据生成、模拟请求等                                                                        |
| [Zero Docs (英文)](https://onemsg.github.io/vertx-zero/)                 | 热心网友提供的一份在线文档：[onemsg](https://github.com/onemsg), 作者BLOG <https://juejin.cn/user/3597257778669592/posts>. |
| [Zero代码示例](https://github.com/silentbalanceyh/vertx-zero-example)      | 旧版本常用的Zero在线代码示例。                                                                                          |
| [旧版英文文档](DOCUMENT.md)                                                  | 旧版 `0.4.8` 文档（英文）。                                                                                         |
| [《Zero冥思录》](https://lang-yu.gitbook.io/zero/)                          | 中文版标准Zero引导教程（总19章）。                                                                                       |
| [《Vert.x逐陆记》](https://lang-yu.gitbook.io/vert-x/)                      | 中文版Vert.x教程 ( In Progress，我很懒，只有前三章 )                                                                      |
| [《Zero云平台白皮书》](https://www.vertx-cloud.cn/document/doc-web/index.html) | 开发实施手册：扩展模块、前端、云端、工具的工程化白皮书。                                                                               |

### 1.3. 元数据标准 ( AMS )

最新版本中 `> 0.9.0`，重新设计了系统最底层，提供了跨框架和跨容器的 **元数据标准**
，详情参考：[《9.标准化》](https://www.vertx-cloud.cn/document/doc-web/index.html#_%E6%A0%87%E5%87%86%E5%8C%96)，此标准可作为
Vert.x 引入独立项目的底层功能使用。

```xml
<!-- 0.9.0 -->
<dependency>
    <artifactId>vertx-ams</artifactId>
    <groupId>cn.vertxup</groupId>
    <version>${ZERO_VERSION}</version>
</dependency>
        <!-- 1.0.0 -->
<dependency>
<artifactId>zero-ams</artifactId>
<groupId>io.zerows</groupId>
<version>${ZERO_VERSION}</version>
</dependency>
```

## 2. 俯瞰

> 换了个名字 `Ecotope` 是因为整个框架目前合计超过 50 多个子项目，而因为强迫症，目前所有项目的主项目命名为 `zero-e`
> 前缀，参考项目语义。

### 2.1. 模块化

**Zero Extension** 扩展包开启了三种模块化场景，您可以根据自己所需单独配置：

* 静态：基于配置数据的基础模块化，每次上模块必须重启容器（开发型、配置型）
* 静态：基于后端元数据规范的模块化，第二管理端可动态配置模块（数据型）。
* OSGI模块化：支持热部署的模块化，不下线模式（Aeon System）。

**Zero Extension** 扩展包中的标准扩展模块如下（开发中心截图）：

![](https://raw.githubusercontent.com/zero-ws/zero-ecotope/master/docs/_image/extension.png)

### 2.2. 完整拓扑

和 `Zero Framework` 配套的框架为 `Aeon Framework` 云原生框架，二者整体架构图如下：

![](https://raw.githubusercontent.com/zero-ws/zero-ecotope/master/docs/_image/arch.png)

### 2.3. 建模设计

建模规范（AMS，纯接口高阶设计）的诞生为整体系统提供了 *云原生、低代码、建模、基础功能* 四大核心区域，使得最新版Zero可支持不同的建模设计：

* ISO规范下的标准化模型，如 `ISO-27001, ISO-20000, ISO-9001`。
* 引入Eclipse EMF，可根据设计模型图生成工程化部署文件以及UML反向工程图。
* `BPMN 2.0` 规范和 `JBPM` 规范提供了完整了企业流程规范。

整体规范基础结构如下**缩略图**：

![](https://raw.githubusercontent.com/zero-ws/zero-ecotope/master/docs/_image/model.png)

### 2.3. 启动流程原理

旧版启动流程原理图如下**缩略图**（两个月前的版本）：

![](https://raw.githubusercontent.com/zero-ws/zero-ecotope/master/docs/_image/booting.png)

## 3. 环境搭建

* **后端脚手架**: <https://gitee.com/silentbalanceyh/vertx-zero-scaffold>
* **前端脚手架**: 使用AI命令 `ai init -name` 直接初始化

### 3.1. pom.xml 中配置

您可以直接在 `pom.xml` 引入如下配置（1.0中换了 `groupId` ）：

**JDK 17+**, vert.x 4.x（1.0.0）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <!-- 继承部分 -->
    <parent>
        <groupId>io.zerows</groupId>
        <artifactId>zero-import</artifactId>
        <version>1.0.0</version>
    </parent>
    <!-- 依赖部分 -->
    <dependencies>
        <dependency>
            <groupId>io.zerows</groupId>
            <artifactId>zero-mini</artifactId>
        </dependency>
    </dependencies>
</project>
```

**JDK 17+**, vert.x 4.x

```xml

<parent>
    <artifactId>vertx-import</artifactId>
    <groupId>cn.vertxup</groupId>
    <version>0.9.0</version>
</parent>
```

**JDK 11+**, vert.x 4.x

```xml

<parent>
    <artifactId>vertx-import</artifactId>
    <groupId>cn.vertxup</groupId>
    <version>0.8.1</version>
</parent>
```

**JDK 8**, vert.x 3.9.x

```xml

<parent>
    <artifactId>vertx-import</artifactId>
    <groupId>cn.vertxup</groupId>
    <version>0.6.2</version>
</parent>
```

### 3.2. 单机启动

基本启动代码如下：

```java
import io.vertx.boot.VertxApplication;
import io.vertx.up.annotations.Up;

@Up
public class Driver {

    public static void main(final String[] args) {
        VertxApplication.run(Driver.class);
    }
}
```

启动后您可以在终端看到类似下边输出 \( 默认端口 `6083` \):

```
[ ZERO ] ZeroHttpAgent Http Server has been started successfully. \
    Endpoint: http://0.0.0.0:6083/
```

### 3.3. 云端启动

云端启动依赖 **Aeon System**, 代码如：

```java
import io.vertx.aeon.AeonApplication;
import io.vertx.up.annotations.Up;

@Up
public class Driver {
    public static void main(final String[] args) {
        AeonApplication.run(Driver.class);
    }
}
```

> 详细教程参考《Zero云端白皮书》。

## 5. 其他

### 5.1. 案例清单

* **Deprecated**: 已放弃
* **In Progress**: 开发和升级开发
* **Running**: 生产环境运行

| 系统名                     | Zero版本 | 系统状态        |
|-------------------------|--------|-------------|
| TLK手机视频管理项目（对接爱奇艺）      | 0.4.6  | Deprecated  |
| ISCCC企业认证评价项目           | 0.8.1  | Running     |
| 数字化协同办公平台               | 0.8.1  | Running     |
| 商机管理系统                  | 0.8.1  | Running     |
| 水果进销存后台管理系统             | 0.8.1  | Running     |
| 政府内部集中采购平台              | 0.8.1  | Running     |
| 发票认证集成连接                | 0.8.1  | Running     |
| CMDB配置化管理系统             | Latest | Running     |
| ITSM流程管理平台              | Latest | Running     |
| 资产报送对接                  | Latest | Running     |
| ISO27000/ISO27001流程管理平台 | Latest | Running     |
| 企业内部培训平台                | Latest | Running     |
| 数据分析考卷系统                | Latest | Running     |
| 医疗器材管理系统                | Latest | In Progress |
| 酒店管理平台                  | Latest | In Progress |
| IoT物联网控制中心              | Latest | In Progress |
| 合规制度管理平台                | Latest | In Progress |

### 5.2. 微信群

使用: `445191171` 加作者微信，拉群.

<img src="https://raw.githubusercontent.com/zero-ws/zero-ecotope/master/docs/_image/chat.jpg" width="258" height="258" alt="作者微信"/>

## DESIGNED IN CHINA（中国设计）



