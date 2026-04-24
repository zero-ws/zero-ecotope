# Static Modeling Guide

> Load this file when the task is about the static modeling chain spanning CRUD metadata, UI form generation, and MBSE modeling support.

## 1. Scope

This file owns the composite scenario:

- static modeling chain
- CRUD metadata as model definition
- UI-side form rendering alignment
- MBSE modeling participation where required

## 2. Owning Modules

- `zero-extension-crud`
- `zero-exmodule-ui`
- `zero-exmodule-mbsecore`

## 3. Key Anchors

- `entity.json`
- `column.json`
- `UiForm`
- UI form services
- MBSE core data/model services

## 4. Source and Resource Path

Read in this order:

```text
static-modeling-guide.md
-> extension-crud-guide.md
-> exmodule-ui-guide.md
-> exmodule-mbsecore-guide.md
-> exact metadata/resources
```

High-value proof targets:

- `entity.json`
- `column.json`
- `UiForm`
- MBSE core plugin/model services
- backend-owned UI configuration records

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` + `r2mo-spec` for shared model meaning plus CRUD/UI/MBSE delivery proof
- `zero-ecotope` alone when the issue is only Zero-side metadata-driven runtime assembly

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- `UiForm` or one model service is already known
- the unresolved point is how CRUD, UI, and MBSE core split implementation responsibility

## 7. AI Agent Rules

- Use CRUD metadata first for static model expression.
- Use UI exmodule when the concern is backend-owned UI configuration.
- Use MBSE core when the concern is modeling-core semantics rather than CRUD-only metadata.
