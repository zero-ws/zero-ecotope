# Weco Capability Guide

> Load this file when the task is about WeCom or WeChat capability, Weco startup actors, or the boundary between platform integration and security integration.

## 1. Scope

This file owns:

- Weco platform capability
- WeCom and WeChat client anchors
- Weco startup actor ownership

It does not own Weco security login flow. That belongs to the security plugin family.

## 2. Owning Modules

- `zero-plugins-weco`
- `zero-plugins-security-weco`

## 3. Key Anchors

- `WeCoActor`
- `WeComClient`
- `WeComClientImpl`
- `WeChatClient`
- `WeChatClientImpl`
- `ApiWeCpActor`
- `ApiWeMpActor`

## 4. Capability Model

The Weco family splits platform communication from auth-specific use:

- platform clients and startup live in `zero-plugins-weco`
- auth and login integration live in `zero-plugins-security-weco`

## 5. Source and Resource Path

Read in this order:

```text
weco-capability-guide.md
-> security-plugin-flow.md if login/auth flow is suspected
-> zero-plugins-weco source
-> zero-plugins-security-weco only if auth-side flow is involved
```

High-value proof targets:

- `WeCoActor`
- `WeComClient`
- `WeComClientImpl`
- `WeChatClient`
- `WeChatClientImpl`
- `ApiWeCpActor`
- `ApiWeMpActor`

## 6. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for platform capability plus auth-plugin boundary
- `zero-ecotope` + `r2mo-rapid` when the unresolved point crosses into Spring-side WeCo delivery/provider ownership

## 7. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one Weco actor/client/auth symbol is already known
- the unresolved point is whether the issue belongs in platform communication or login/auth adaptation

## 8. AI Agent Rules

- Diagnose platform capability and security flow separately.
- Inspect `WeCoActor` first when the base platform integration does not initialize.
