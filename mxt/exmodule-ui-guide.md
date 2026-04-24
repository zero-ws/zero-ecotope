# Exmodule UI Guide

> Load this file when the task is about backend-owned UI configuration center behavior in `zero-exmodule-ui`.

## 1. Scope

`zero-exmodule-ui` owns backend-side UI configuration semantics.

## 2. Verified Anchors

The module centers around UI configuration records and services such as:

- forms
- fields
- options
- pages
- backend-fed UI assembly and lookup

Typical provider/service ownership should be read through:

- `Form*`
- `Field*`
- `Option*`
- `Page*`
- `ExtensionUISource`
- `MDUIActor`

## 3. Boundary

Use UI exmodule when the issue is about backend-owned UI metadata/configuration.

Do not use it for:

- frontend rendering implementation details
- React/Tauri/UI component code itself

For frontend rendering, continue into `zero-ui/src/extension/` and `dual-side-development.md`.

## 4. Source and Resource Path

Read in this order:

```text
exmodule-ui-guide.md
-> static-modeling-guide.md when CRUD/form metadata is involved
-> modulat-ui-unified-guide.md when modular config also participates
-> zero-exmodule-ui source/resources
```

High-value proof targets:

- `Form*`
- `Field*`
- `Option*`
- `Page*`
- `ExtensionUISource`
- `MDUIActor`
- backend-owned UI configuration resources

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for backend-owned UI configuration behavior
- `zero-ecotope` + `r2mo-spec` when shared payload/model meaning of forms and fields must be confirmed

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one `Form*`, `Field*`, `Option*`, or `Page*` symbol is already known
- the unresolved point is whether the behavior belongs in backend-owned UI metadata or frontend rendering/application code
