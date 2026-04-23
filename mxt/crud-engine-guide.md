# CRUD Engine Guide

> Load this file when the task is about standard Zero module interfaces, metadata-driven CRUD, or deciding whether handwritten DPA is necessary.

## 1. Scope

This file owns:

- CRUD-engine-first decision rules
- standard CRUD transport coverage
- `entity.json` as backend metadata entrypoint
- zero-code and low-code decision boundaries

It does not own:

- generic DPA boundaries
- raw DBE syntax
- RBAC resource semantics

## 2. Core Rule

For standard interface scenarios, choose CRUD engine first.

Do not choose handwritten DPA first unless the requirement exceeds standardized CRUD semantics.

## 3. Coverage

The CRUD engine already covers standardized transport paths such as:

- create
- search
- existing / missing validation
- fetch by id
- fetch by tenant scope
- update by id
- batch update
- delete by id
- batch delete
- import / export
- view and column metadata endpoints

Practical implication:

```text
Standard data-management modules do not need handwritten transport code by default.
```

## 4. Metadata Entry Point

For CRUD-engine modules, the main backend entrypoint is:

```text
plugins/{module}/model/{identifier}/entity.json
```

It commonly defines:

- model name
- DAO binding
- unique-key strategy
- default field initialization
- business tags and categorization

Rule:

```text
If the module fits CRUD shape, start from metadata before designing services.
```

## 5. Decision Order

Evaluate in this order:

1. Can CRUD engine cover the requirement with standard routes?
2. Can the requirement be expressed by metadata, resources, or SPI hooks?
3. Only then fall back to handwritten DPA.

## 6. Good CRUD-Engine Cases

Prefer CRUD engine when the module is mainly:

- standard single-entity CRUD
- tenant-safe search/list/detail
- batch update or batch delete
- uniqueness validation
- import/export around tabular data
- dynamic column/view metadata delivery
- standard default/audit preprocessing

## 7. Must-Handwrite Cases

Use handwritten DPA when the requirement needs:

- cross-aggregate orchestration
- custom transactions not representable by engine hooks
- bespoke event choreography
- domain-specific state machines beyond resource configuration
- imperative algorithms or external integrations as the core value

## 8. Agent Rules

- Do not build a custom transport layer for ordinary CRUD first.
- Do not ignore `entity.json` when the requirement is standard data management.
- If metadata already expresses the behavior, keep the solution metadata-driven.
