# Zero Ecotope Framework

[![Maven Central](https://img.shields.io/maven-central/v/io.zerows/rachel-momo.svg?label=Rachel%20Momo&style=for-the-badge&color=blue)](https://mvnrepository.com/artifact/io.zerows/rachel-momo)  [![Maven Central](https://img.shields.io/maven-central/v/io.zerows/r2mo-rapid.svg?label=R2MO%20Rapid&style=for-the-badge&color=green)](https://mvnrepository.com/artifact/io.zerows/r2mo-rapid)
Zero Waiting

> For [Rachel Momo](https://www.weibo.com/maoxiaotong0216)

![ZERO](docs/images/logo.jpeg)

## 0. 引导

- 教程：<https://www.yuque.com/jiezizhu/r2mo>
    - [>> 快速开始](https://www.yuque.com/jiezizhu/r2mo/gs6qygvsorlgkxb2)
- 教程地图：<https://kumu.io/LangYu1017/zero>
- 示例：<https://gitee.com/zero-ws/zero-rachel-momo>

![Zero Ecotope](docs/images/zero-ecotope.png)

---

### 0.1. 新版说明

- 为兼容 Spring Boot 开发人员和 Vert.x
  开发人员，重新设计底层抽象框架：[Rapid R2MO](https://gitee.com/silentbalanceyh/r2mo-rapid)。
- 提供两套原生结构专注于 Spring / Vert.x 开发，若只是想要短平快地开发、实施、交付中小项目，可考虑使用。

配置管理分为：本地配置和远程配置（Nacos），配置结构参考：[vertx.yml](https://gitee.com/zero-ws/zero-rachel-momo/blob/master/rachel-momo-app/app-zero-extension/src/main/resources/vertx.yml)
，本次重构的配置结构契合 Spring Boot 中的
`application.yml` 结构，方便快速上手。

- 提供 `HActor` 插件/模块 启动器，近似于 `-starter` 模型，更方便扩展开发。
- 提供快速脚手架初始化工具：[Zero Ai](https://www.vertxai.cn)。
- 让 Spring 和 Vert.x（Zero）可无缝实现基于 Dubbo 的微服务通信。

### 0.2. 场景说明

| 场景类型     | 后端                                                                | 前端                                                     |
|----------|-------------------------------------------------------------------|--------------------------------------------------------|
| 管理密集型    | [R2MO Rapid Spring](https://gitee.com/silentbalanceyh/r2mo-rapid) | [Zero Ui](https://www.vertxui.cn/)                     |
| 运算、交互密集型 | [Zero Epoch](https://www.zerows.io/)                              | [R2MO Web](https://gitee.com/silentbalanceyh/r2mo-web) |                       

> 精力有限，Vert.x 本在国内属于小众，无法提供更多关于 Zero 的测评数据，现阶段只在部分项目、公司产品研发中使用。

---

## 1. 模块依赖

### 1.1. 源文件

- [整体依赖图](docs/dependency.md)
- [R2MO依赖图](docs/dependency-r2mo-compile.md)
- [ZERO依赖图](docs/dependency-zero-compile.md)

### 1.2. ZERO

![Zero Ecotope 主架构](.r2mo/zero-arch.svg)

### 1.3. R2MO & ZERO

![Zero R2MO 主架构](.r2mo/zero-r2mo-arch.svg)

---

## 1. 快速开始

### 1.1. 简化用法

```java
package io.zerows.momo.app;

import io.zerows.boot.VertxApplication;
import io.zerows.epoch.annotations.Up;

@Up
public class BasicApplication {
    // 无配置模式可直接启动
    public static void main(final String[] args) {
        VertxApplication.run(BasicApplication.class, args);
    }
}
```

### 1.2. 参考案例

地址：<https://gitee.com/zero-ws/zero-rachel-momo>

项目结构：

- 📚️ 应用案例（ `app-rachel-momo` ）：
    - [x] ☘️ `app-spring-example`
        - [x] ☘️ `app-spring-auth`：Spring 安全认证专用 Demo。
        - [x] ☘️ `app-spring-basic`：Spring 基础使用 Demo。
        - [x] ☘️ `app-spring-test`：Spring 基础应用 Demo。
    - [x] 🍓 `app-vertx-example`
        - [x] 🍓 `app-vertx-rapid`：Vert.x 基础应用。
    - [x] 🧊 `app-zero-example`
        - [x] 🧊 `app-zero-basic`：Zero Core 最小化应用。
        - [x] 🧊 `app-zero-extension`：Zero Extension 扩展应用（带业务模块）。
        - [x] 🧊 `app-zero-module`：Zero 自定义模块化开发，扩展模块。
        - [x] 🧊 `app-zero-secure`：Zero 安全测试模块。
        - [x] 🧊 `app-zero-service`：Zero 微服务应用。
        - [x] 🧊 `app-zero-underway`：Zero 任务测试模块。
    - [x] 🐣 `app-unit-example`：公共模块。
        - [x] 🐣 `unit-zero-common`：单元测试基础
        - [x] 🐣 `unit-zero-config-l`：本地配置测试
        - [x] 🐣 `unit-zero-config-r`：远程配置（Nacos）测试

### 1.3. Zero Extension 接口

地址：<https://zerows.apifox.cn/>

#### 属性标记

| 属性标记 | 说明                                              |
|------|-------------------------------------------------|
| 🥏   | Java 组件属性，一般映射成 Java 类名                         |
| 🧫   | 配置专用属性，通常对应 `JsonObject / JsonArray` 数据结构       |
| 🧊   | 业务属性，此属性一般呈现于界面提供给用户使用                          |
| 🔑   | 标识属性，可标识当前数据记录，标识属性不包含范围如 `APP_ID / TENANT_ID`等 |
| 🧬   | 范围标识符，用于标记所属范围                                  |
| 🔨   | 系统属性，通常为系统自动属性，部署运维专用                           |
| 🫆   | Audit 属性，通常为 `?At / ?By`                        |
| ☘️   | 关联属性                                            |
| 🔵   | （后缀）布尔值，Json格式                                  |
| 🔴   | （后缀）数值，Json格式                                   |

#### 时序图标记

- 🧪 ：Agent / Worker 基本方法
- 🟧：EventBus 地址
- 🧬：业务逻辑方法
- 🪼：数据库 DBE方法
- 🧩：组件 / 插件 / SPI专用逻辑

---

## 2. 微信交流

使用: `445191171` 或下方二维码加作者：

<img src="docs/images/wechat.png" width="258" height="258" alt="作者微信"/>

## DESIGNED IN CHINA（中国设计）



