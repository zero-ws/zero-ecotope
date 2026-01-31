---
method: GET
path: /app/name/{name}
---

## API

```yaml
summary: "登录主界面根据应用名读取应用基础数据"
operationId: "appByName"
description: |
  场景描述：
  1. 入口读取应用基础数据（公开接口）。
  2. 此时读取不考虑 appSecret / appKey 等敏感字段。
  3. 根据 X_TENANT 读取应用列表，仅返回 status = RUNNING 的应用。
  4. 启动的前端 Z_APP 环境变量标识了默认应用 (isDefault = true)。
tags:
  - 应用管理
parameters:
  - name: name
    in: path
    description: "应用唯一名称 (Global Unique Name)"
    required: true
    schema:
      type: string
responses:
  "200":
    description: "成功返回应用基础信息"
    content:
      application/json:
        schema:
          $ref: "#/components/schemas/XApp"
  "404":
    description: "未找到指定名称的应用或应用未运行"
```

## Request

---

## Response

### Ok

### Failure