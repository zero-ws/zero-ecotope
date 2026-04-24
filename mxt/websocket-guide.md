# WebSocket Capability Guide

> Load this file when the task is about active push, WebSocket capability ownership, or framework-level socket integration.

## 1. Scope

This file owns:

- WebSocket plugin ownership
- active-push capability placement
- plugin-layer boundary for socket integration

## 2. Owning Modules

- `zero-plugins-websocket`

## 3. Capability Model

WebSocket support belongs to the capability plugin layer.
It is the framework owner for reusable active-push transport capability.

Business subscription rules or domain event semantics belong above this layer.

## 4. Source and Resource Path

Read in this order:

```text
websocket-guide.md
-> plugin-layer-map.md
-> zero-plugins-websocket source
-> consuming exmodule or app only if domain push semantics are unresolved
```

High-value proof targets:

- WebSocket plugin bootstrap classes
- active-push transport adapters
- plugin configuration resources

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for transport capability ownership
- `zero-ecotope` + `r2mo-rapid` only when shared delivery/provider split must also be compared

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one socket or push symbol is already known
- the unresolved point is plugin transport ownership versus domain push policy

## 7. AI Agent Rules

- Keep transport integration in the plugin.
- Keep domain push policy in exmodules or apps.
- Start from the plugin module tree when the task says “active push” or “WebSocket”.
