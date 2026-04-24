# Email Capability Guide

> Load this file when the task is about email delivery capability, email startup actors, or the boundary between email plugin and email-auth security plugin.

## 1. Scope

This file owns:

- email plugin capability
- email startup anchors
- email client/provider anchors

It does not own email-code authentication semantics. That belongs to the security plugin family.

## 2. Owning Modules

- `zero-plugins-email`
- `zero-plugins-security-email`

## 3. Key Anchors

- `EmailActor`
- `EmailClient`
- `EmailClientImpl`
- `EmailAddOn`
- `EmailAuthActor`

## 4. Capability Model

Email delivery and email-code auth are related but not identical:

- `zero-plugins-email` owns transport and client capability
- `zero-plugins-security-email` owns auth-facing email flow

## 5. Source and Resource Path

Read in this order:

```text
email-capability-guide.md
-> security-plugin-flow.md if auth-side flow is suspected
-> zero-plugins-email source
-> zero-plugins-security-email only if login/auth flow is involved
```

High-value proof targets:

- `EmailActor`
- `EmailClient`
- `EmailClientImpl`
- `EmailAddOn`
- `EmailAuthActor`

## 6. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for plugin capability plus auth-plugin boundary
- `zero-ecotope` + `r2mo-rapid` when the unresolved point crosses into shared delivery/provider ownership on the Spring side

## 7. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one email actor/client/auth symbol is already known
- the unresolved point is whether the problem belongs in transport capability or auth-side adaptation

## 8. AI Agent Rules

- Separate delivery failures from auth-code flow failures.
- Inspect `EmailActor` for plugin boot and `EmailAuthActor` for auth-side behavior.
