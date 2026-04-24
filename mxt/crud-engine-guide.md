# CRUD Engine Guide

> Load this file when the task is about standard Zero module interfaces, metadata-driven CRUD, or deciding whether handwritten DPA is actually necessary.

## 1. Scope

This file owns:

- CRUD-engine-first decision rules
- metadata-driven transport coverage
- `entity.json` as backend metadata entrypoint
- the boundary between standard CRUD delivery and handwritten DPA

It does not own:

- generic SPI contract ownership
- raw DBE syntax details
- one exmodule's domain semantics

## 2. The Real Entry Layer

The CRUD engine is not just "some generated endpoints". It is a reusable execution pipeline centered around:

- `MDCRUDManager`
- `IxSetup*`
- `Ix*`
- `Pre*`
- `Agonic*`
- `Operate*`

That means the first question is not "which actor should I edit?"
The first question is:

```text
Can the existing CRUD pipeline or module metadata already express this behavior?
```

## 3. Coverage Surface

The current engine already covers standardized transport such as:

- create
- search/list
- fetch by id
- existing/missing validation
- update by id
- batch update
- delete by id
- batch delete
- import/export
- view and column metadata
- standard audit/file preprocessing

Practical rule:

```text
Standard data-management modules should not start from handwritten DPA.
```

## 4. Metadata First

For CRUD-engine modules, the main backend metadata surface is still:

```text
plugins/{module}/model/{identifier}/entity.json
```

Common responsibilities:

- entity binding
- key strategy
- field defaults
- metadata-driven route behavior
- standardized CRUD module description

If the requirement fits standard data management, inspect metadata and CRUD boot first.

## 5. Verified Extension-Crud Anchors

Confirmed source anchors:

- `zero-extension-crud/.../boot/MDCRUDManager.java`
- `zero-extension-crud/.../boot/MDCRUDActor.java`
- `zero-extension-crud/.../boot/IxSetupModule.java`
- `zero-extension-crud/.../common/Ix.java`
- `zero-extension-crud/.../uca/AgonicADBCreate.java`
- `zero-extension-crud/.../uca/AgonicADBUpdate.java`
- `zero-extension-crud/.../uca/AgonicADBDelete.java`
- `zero-extension-crud/.../uca/AgonicADBImport.java`
- `zero-extension-crud/.../uca/input/PreAudit*.java`
- `zero-extension-crud/.../uca/input/PreFile*.java`
- `zero-extension-crud/.../uca/input/PreExcel.java`
- `zero-extension-crud/.../plugins/DocExtensionActor.java`

## 6. Decision Order

1. Can CRUD engine already cover the route?
2. Can metadata or reusable preprocessors express the new rule?
3. Can an SPI or before/after hook express it?
4. Only then fall back to handwritten DPA.

## 7. Good CRUD-Engine Cases

Prefer CRUD engine when the module mainly needs:

- standard single-entity CRUD
- tenant-safe search/list/detail
- uniqueness validation
- standard pre-audit/default/user/file handling
- table-like import/export
- dynamic list/view metadata

## 8. Handwritten DPA Cases

Use handwritten DPA when the requirement needs:

- cross-aggregate orchestration
- custom event choreography
- non-standard long business transactions
- imperative algorithms as the main value
- workflow/report/ACL-style orchestration beyond CRUD metadata

## 9. Common Mistakes

- writing one-off actors for standard CRUD routes
- duplicating `PreAudit*`/`PreFile*` logic inside an actor
- ignoring module metadata and hardcoding behavior
- editing exmodule services when the reusable rule belongs in `zero-extension-crud`
