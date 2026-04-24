# Exmodule Integration Guide

> Load this file when the task is about reusable integration directories, message integration, or integration-domain business semantics in `zero-exmodule-integration`.

## 1. Scope

`zero-exmodule-integration` owns business-facing integration semantics, not low-level protocol adapters.

## 2. Verified Anchors

- API:
  - `DirActor`
  - `DirAgent`
  - `MessageActor`
  - `MessageAgent`
- Domain:
  - `DirStub`
  - `MessageStub`
- Provider:
  - `DirService`
  - `MessageService`
  - `MDIntegrationActor`
  - `ExtensionIntegrationSource`

## 3. Boundary

Use Integration when the issue is about reusable directory/message business behavior.

Do not use it for:

- raw file transfer/storage provider choice
- email/SMS/WebSocket transport
- generic OAuth2/HTTP protocol adapters

## 4. Source and Resource Path

Read in this order:

```text
exmodule-integration-guide.md
-> attachment-storage-integration-guide.md when storage connectors are involved
-> zero-exmodule-integration source/resources
-> r2mo-rapid shared IO or delivery docs only if transport/provider ownership is unresolved
```

High-value proof targets:

- `DirActor`
- `MessageActor`
- `DirStub`
- `MessageStub`
- `DirService`
- `MessageService`
- `MDIntegrationActor`
- integration directory/message resources

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for directory/message business semantics
- `zero-ecotope` + `r2mo-rapid` when the issue crosses into shared IO, transport, or delivery-provider ownership

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one integration symbol is already known
- the unresolved point is whether the behavior stays in integration business semantics or falls into transport/provider infrastructure
