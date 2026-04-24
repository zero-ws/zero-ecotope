# Exmodule LBS Guide

> Load this file when the task is about reusable location/geography semantics in `zero-exmodule-lbs`.

## 1. Scope

`zero-exmodule-lbs` owns reusable location-domain behavior.

## 2. Verified Anchors

- API:
  - `QueryActor`
- Domain:
  - `LocationStub`
- Provider:
  - `LocationService`
  - `MDLbsActor`
  - `ExtensionLBSSource`

## 3. Boundary

Use LBS for geography/location hierarchy semantics.

Do not use it for:

- generic datasource registration
- generic CRUD pipeline ownership

## 4. Source and Resource Path

Read in this order:

```text
exmodule-lbs-guide.md
-> zero-exmodule-lbs source/resources
-> r2mo-spec metadata/openapi only if shared location semantics are unresolved
```

High-value proof targets:

- `QueryActor`
- `LocationStub`
- `LocationService`
- `MDLbsActor`
- location model/resources

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for reusable location runtime behavior
- `zero-ecotope` + `r2mo-spec` when shared location model meaning must be confirmed

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- `LocationStub` or `LocationService` is already known
- the unresolved point is cross-module caller spread or provider ownership
