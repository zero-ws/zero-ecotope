# Zero Ecotope Framework

[![Maven Central](https://img.shields.io/maven-central/v/io.zerows/rachel-momo.svg?label=Rachel%20Momo&style=for-the-badge&color=blue)](https://mvnrepository.com/artifact/io.zerows/rachel-momo)  [![Maven Central](https://img.shields.io/maven-central/v/io.zerows/r2mo-rapid.svg?label=R2MO%20Rapid&style=for-the-badge&color=green)](https://mvnrepository.com/artifact/io.zerows/r2mo-rapid)
Zero Waiting

> For Rachel Momo

## 0. 引导

目前未发布正式版本，等手中项目全部迁移完成之后会发布 `1.0.0` 正式版本，有兴趣的朋友可以参与测试，测试项目地址：<https://gitee.com/zero-ws/zero-rachel-momo>。

### 0.1. 主页信息

- （后端）Zero Ecotope：<https://www.zerows.io>
- （前端）Zero UI：<https://www.vertxui.cn>
- （工具）Zero AI：<https://www.vertxai.cn>
- （标准）Zero Schema：<https://www.vertx-cloud.cn>

### 0.2. 新版说明

- 为兼容 Spring Boot 开发人员和 Vert.x 开发人员，重新设计底层抽象框架：[Rapid R2MO](https://gitee.com/silentbalanceyh/r2mo-rapid)。
- 提供两套原生结构专注于 Spring / Vert.x 开发，若只是想要短平快地开发、实施、交付中小项目，可考虑使用。
- 配置管理分为：本地配置和远程配置（Nacos），配置结构参考：[vertx.yml](https://gitee.com/zero-ws/zero-rachel-momo/blob/master/rachel-momo-app/rachel-momo-app-extension/src/main/resources/vertx.yml)，本次重构的配置结构契合 Spring Boot 中的
  `application.yml` 结构，方便快速上手。
- 提供 `HActor` 插件/模块 启动器，近似于 `-starter` 模型，更方便扩展开发。
- 提供快速脚手架初始化工具：[Zero Ai](https://www.vertxai.cn)。
- 让 Spring 和 Vert.x（Zero）可无缝实现基于 Dubbo 的微服务通信。

### 0.3. 场景说明

| 场景类型     | 后端                                                                | 前端                                                     |
|----------|-------------------------------------------------------------------|--------------------------------------------------------|
| 管理密集型    | [R2MO Rapid Spring](https://gitee.com/silentbalanceyh/r2mo-rapid) | [Zero Ui](https://www.vertxui.cn/)                     |
| 运算、交互密集型 | [Zero Epoch](https://www.zerows.io/)                              | [R2MO Web](https://gitee.com/silentbalanceyh/r2mo-web) |                       

> 精力有限，Vert.x 本在国内属于小众，无法提供更多关于 Zero 的测评数据，现阶段只在部分项目、公司产品研发中使用。

<hr/>

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

- 应用案例（ `rachel-momo-app` ）：
    - [x] `rachel-momo-app-basic`：Zero Core 最小化应用。
    - [x] `rachel-momo-app-extension`：Zero Extension 扩展应用（带业务模块），内置 `vertx.yml` 格式。
    - [x] `rachel-momo-app-module`：Zero 自定义模块化开发，扩展模块。
    - [x] `rachel-momo-app-rapid-spring`：R2MO Rapid Spring Boot 应用。
    - [x] `rachel-momo-app-rapid-vertx`：R2MO Rapid Vert.x 应用。
    - [x] `rachel-momo-app-service`：Zero 微服务应用。
- 测试用例（ `rachel-momo-rapid` ）：
    - [x] `rachel-momo-rapid-spring`：R2MO Rapid Spring 测试用例。
    - [x] `rachel-momo-rapid-vertx`：R2MO Rapid Vert.x 测试用例。
- 测试套件（ `rachel-momo-suite` ）：
    - [x] `rachel-momo-suite-config-l`：本地配置管理测试套件
    - [x] `rachel-momo-suite-config-r`：远程配置（Nacos）管理测试套件
    - [x] `rachel-momo-suite-zero`：Zero Core 专用测试套件

### 1.3. 项目初始化

最新版：![npm version](https://img.shields.io/npm/v/zero-ai.svg)

```bash
# 安装自动化工具
npm install -g zero-ai

# 初始化 Spring 项目脚手架
ai spring -n app-spring
# 初始化 Zero 项目脚手架（开发中）
ai app -n app-zero
```

- Windows 版本还在开发中。
- Spring 脚手架只是基于 R2MO Rapid 的快速初始化，和 Zero 无关。

## 2. 相关文档

### 2.1. 相关链接

版本比较旧，新版可直接参考测试项目中的 Demo。

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

### 2.2. 微信群

使用: `445191171` 加作者微信，拉群.

<img src="https://raw.githubusercontent.com/zero-ws/zero-ecotope/master/docs/_image/chat.jpg" width="258" height="258" alt="作者微信"/>

## DESIGNED IN CHINA（中国设计）



