# Exmodule MBSE API Guide

> Load this file when the task is about dynamic API exposure, URI/task/job-facing MBSE semantics in `zero-exmodule-mbseapi`.

## 1. Scope

`zero-exmodule-mbseapi` owns MBSE-facing API/service semantics above the raw job/runtime layer.

## 2. Verified Anchors

- API:
  - `TaskActor`
  - `UriActor`
- Domain:
  - `AmbientStub`
  - `JobStub`
- Provider:
  - `JobService`
  - `AmbientService`
  - `MDMBSEApiActor`
  - `ExtensionMBSEApiSource`

## 3. Boundary

Use this module when the issue is about MBSE task/URI/job domain semantics.

Do not use it for:

- generic job runtime ownership
- spec/model semantics that belong in `r2mo-spec`

## 4. Source and Resource Path

Read in this order:

```text
exmodule-mbseapi-guide.md
-> job-model-guide.md when job lifecycle is involved
-> zero-exmodule-mbseapi source/resources
-> r2mo-spec only if shared contract meaning is unresolved
```

High-value proof targets:

- `TaskActor`
- `UriActor`
- `JobStub`
- `JobService`
- `MDMBSEApiActor`
- module-owned URI/task/job resources

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for MBSE API runtime ownership
- `zero-ecotope` + `r2mo-spec` when task/URI/job payload meaning must be confirmed as shared contracts

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- `TaskActor`, `UriActor`, or `JobService` is already known
- the unresolved point is whether the problem belongs in MBSE API business semantics or lower job/runtime layers
