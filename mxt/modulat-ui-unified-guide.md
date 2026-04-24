# Modulat UI Unified Guide

> Load this file when the task is about modular configuration service together with unified UI handling across modulat and UI modules.

## 1. Scope

This file owns the composite scenario:

- modular configuration service
- unified UI handling for modular data
- cross-module coordination between modulat and UI

## 2. Owning Modules

- `zero-exmodule-modulat`
- `zero-exmodule-ui`

## 3. Key Anchors

- `BagActor`
- `BagArgActor`
- `UiActor`
- `FormActor`
- `UiValve`

## 4. Source and Resource Path

Read in this order:

```text
modulat-ui-unified-guide.md
-> exmodule-modulat-guide.md
-> exmodule-ui-guide.md
-> modulat-dynamic-operation-rules.md when bag/block config matters
-> exact source/resources
```

High-value proof targets:

- `BagActor`, `BagArgActor`
- `UiActor`, `FormActor`
- `UiValve`
- bag/block/open/full configuration resources

## 5. Boundary

Use this guide when both are involved:

- modular configuration ownership
- backend-owned unified UI exposure

Do not use it for:

- frontend rendering implementation details
- one module's isolated ownership when the cross-module seam is not actually in question

## 6. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for modulat plus UI exmodule coordination
- `zero-ecotope` + `r2mo-spec` when a shared model/form contract must be confirmed

## 7. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- `BagActor`, `UiActor`, or `UiValve` is already known
- the unresolved point is how the config/UI chain is split between modulat and UI modules

## 8. AI Agent Rules

- Keep configuration ownership in modulat.
- Keep backend-owned UI configuration exposure in UI exmodule.
- Use this file when the problem spans both modules rather than belonging to only one of them.
