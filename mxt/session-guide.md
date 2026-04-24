# Session Guide

> Load this file when the task is about `zero-plugins-session`, session store wiring, session handler setup, or session capability boundaries.

## 1. Scope

This file owns `zero-plugins-session`.

It owns:

- session plugin startup
- session client and provider wiring
- session store and handler anchors
- plugin-level session capability

It does not own:

- authentication protocol rules
- RBAC permission rules
- application-specific login behavior

## 2. Owning Module

- `zero-plugins-equip/zero-plugins-session`

Verified graph/source anchors:

- `SessionActor`
- `SessionClient`
- `SessionClientImpl`
- `SessionManager`
- `SessionProvider`
- `SessionType`
- `SessionUtil`

## 3. Responsibility Model

Session is a transport/runtime capability.

It supports:

- setting up the session handler
- selecting or wiring session store behavior
- exposing a framework client for session access

It should not decide:

- how a user authenticates
- which permission model applies
- how a business module interprets a session

## 4. Source and Resource Path

Read in this order:

```text
session-guide.md
-> security-plugin-flow.md when auth flow is suspected
-> zero-plugins-session source
-> RBAC docs only if authorization semantics are the unresolved point
```

High-value proof targets:

- `SessionActor`
- `SessionClient`
- `SessionClientImpl`
- `SessionManager`
- `SessionProvider`
- `SessionType`
- `SessionUtil`

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for session capability ownership
- `zero-ecotope` + `r2mo-rapid` only when session/runtime behavior must be compared across runtime lines

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one session symbol is already known
- the unresolved point is structural spread between session boot, provider wiring, and consuming auth/runtime modules

## 7. AI Agent Rules

- Start from `SessionActor` for boot and config behavior.
- Route authentication issues to `security-plugin-flow.md`.
- Route authorization issues to `exmodule-rbac-guide.md` or `backend-rbac-rules.md`.
