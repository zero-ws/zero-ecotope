# SMS Capability Guide

> Load this file when the task is about SMS delivery capability, SMS startup actors, or the boundary between SMS plugin and SMS-auth security plugin.

## 1. Scope

This file owns:

- SMS plugin capability
- SMS startup anchors
- SMS client/provider anchors

It does not own SMS-code authentication semantics. That belongs to the security plugin family.

## 2. Owning Modules

- `zero-plugins-sms`
- `zero-plugins-security-sms`

## 3. Key Anchors

- `SmsActor`
- `SmsClient`
- `SmsClientImpl`
- `SmsAddOn`
- `SmsAuthActor`

## 4. Capability Model

SMS delivery and SMS-based auth are split:

- delivery belongs to the core SMS plugin
- auth flow belongs to the security SMS plugin

## 5. Source and Resource Path

Read in this order:

```text
sms-capability-guide.md
-> security-plugin-flow.md if login/auth flow is suspected
-> zero-plugins-sms source
-> zero-plugins-security-sms only if auth-side flow is involved
```

High-value proof targets:

- `SmsActor`
- `SmsClient`
- `SmsClientImpl`
- `SmsAddOn`
- `SmsAuthActor`

## 6. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for plugin capability plus auth-plugin boundary
- `zero-ecotope` + `r2mo-rapid` when the unresolved point crosses into Spring-side SMS delivery/provider ownership

## 7. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one SMS actor/client/auth symbol is already known
- the unresolved point is whether the issue belongs in delivery transport or auth-side adaptation

## 8. AI Agent Rules

- Diagnose delivery and authentication as separate concerns.
- Inspect `SmsActor` for transport startup and `SmsAuthActor` for login-side behavior.
