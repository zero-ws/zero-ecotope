# Zero.Core.Runtime.Metadata

> 开发参考：<https://gitee.com/zero-ws/zero-rapid-fabric-runtime>

## 1. 基础说明

### 1.1. 环境

```bash
# 环境初始化
mvn -Pdev clean package install
# 编译/调试
mvn clean package install
```

### 1.2. 依赖

```bash
# 查看依赖
git submodule
# 依赖清单
git submodule add git@gitee.com:zero-ws/zero-rapid-build-running.git refs/Zero.Rapid.Build.Runtime
```

## 2. 模块信息

### 2.1. 服务清单

| 接口              | 服务名   | 说明                               |
|-----------------|-------|----------------------------------|
| `ExceptionDesk` | 异常服务台 | 用于管理所有全局异常专用服务，其他异常会注册模块内执行存储管理。 |

