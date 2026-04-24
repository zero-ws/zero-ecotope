# Attachment Storage Integration Guide

> Load this file when the task is about attachment upload, storage abstraction, or FTP-style integration attached to ambient and integration modules.

## 1. Scope

This file owns the composite scenario:

- attachment upload
- storage metadata
- integration-driven remote storage such as FTP-style extension

## 2. Owning Modules

- `zero-exmodule-ambient`
- `zero-exmodule-integration`
- shared SPI such as `ExAttachment`

## 3. Key Anchors

- `AttachActor`
- `ExAttachment`
- integration directory and message services
- storage-related environment variables in `EnvironmentVariable`

## 4. Source and Resource Path

Read in this order:

```text
attachment-storage-integration-guide.md
-> attachment-storage-configurable-storage.md
-> exmodule-ambient-guide.md or exmodule-integration-guide.md
-> exact source/resources
```

High-value proof targets:

- ambient attachment actors and services
- integration directory/message providers
- upload/download/session-related resources
- environment contract entries for storage endpoints

## 5. Boundary

Use this guide when the task spans both:

- business-owned attachment metadata
- integration-facing external storage or connector behavior

Do not use it for:

- raw `HFS` / `HStore` / `RFS` provider internals
- Spring MVC multipart landing
- one project's private upload policy

For IO-provider ownership, continue into `../r2mo-rapid/mxt/hfs-hstore-usage.md`.

## 6. Pairwise Handling

Preferred pairs:

- `zero-ecotope` + `r2mo-rapid` for business attachment semantics plus shared IO provider proof
- `zero-ecotope` + `r2mo-spec` only when shared contract meaning of attachment models is the unresolved point

## 7. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- `AttachActor`, `ExAttachment`, or one integration service is already known
- the unresolved point is structural ownership between ambient and integration

## 8. AI Agent Rules

- Keep attachment metadata and ambient ownership in ambient.
- Keep external connector behavior in integration.
- Keep transport or storage protocol capability outside business modules unless the abstraction is business-owned.
