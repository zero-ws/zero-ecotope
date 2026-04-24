# Exmodule Modulat Guide

> Load this file when the task is about bag/block modular configuration, runtime modular app config, or reusable modular-configuration behavior in `zero-exmodule-modulat`.

## 1. Scope

`zero-exmodule-modulat` owns reusable modular configuration semantics.

## 2. Verified Anchors

- API:
  - `BagActor`
  - `BagAgent`
  - `BagArgActor`
  - `BagArgAgent`
- Domain:
  - `BagStub`
  - `BagArgStub`
  - `BlockStub`
- Provider:
  - `BagService`
  - `BagArgService`
  - `BlockService`
  - `MDModulatActor`
  - `ExtensionModulatSource`

## 3. Boundary

Use Modulat when the rule is about:

- `B_BAG`
- `B_BLOCK`
- modular app config
- open/full config semantics
- bag/block-level runtime configuration

Use `modulat-dynamic-operation-rules.md` for deeper dynamic-operation rules.

## 4. Source and Resource Path

Read in this order:

```text
exmodule-modulat-guide.md
-> modulat-dynamic-operation-rules.md
-> modulat-ui-unified-guide.md only if UI exposure is also involved
-> zero-exmodule-modulat source/resources
```

High-value proof targets:

- `BagActor`
- `BagArgActor`
- `BagStub`
- `BlockStub`
- `BagService`
- `BlockService`
- `MDModulatActor`
- bag/block/open/full-config resources

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for modular configuration ownership
- `zero-ecotope` + `r2mo-spec` when bag/block related shared model or form semantics must be confirmed

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- `BagActor`, `BagStub`, or `BlockService` is already known
- the unresolved point is structural ownership between config records, runtime assembly, and UI exposure
