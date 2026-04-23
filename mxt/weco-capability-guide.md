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

## 5. AI Agent Rules

- Diagnose platform capability and security flow separately.
- Inspect `WeCoActor` first when the base platform integration does not initialize.
