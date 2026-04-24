# Zero Version Guide

> Load this file when the task is about BOM ownership, version alignment, artifact exposure, or dependency-management boundaries in Zero Ecotope.

## 1. Scope

This file owns:

- `zero-version` module purpose
- version BOM split
- dependency-management ownership
- where to verify extension and plugin exposure

It does not own runtime behavior or plugin semantics.

## 2. Owning Modules

| Module | Responsibility | When AI Agent Should Inspect |
|---|---|---|
| `zero-version` | Parent BOM POM: top-level version alignment and module aggregation | Version conflict across layers, new module not in BOM |
| `zero-version-epoch` | Managed dependencies for `zero-epoch-*`, `zero-overlay`, core runtime | Core runtime dependency version mismatch |
| `zero-version-plugins` | Managed dependencies for `zero-plugins-*` capability plugins | Plugin dependency version mismatch, new plugin not exposed |
| `zero-version-extension` | Managed dependencies for `zero-extension-*` and `zero-exmodule-*` | Extension/exmodule dependency version mismatch, new exmodule not exposed |
| `zero-version-overlay` | Managed dependencies for `zero-overlay` bridge contracts | R2MO bridge dependency version issues |

## 3. Reading Rule

Use `zero-version` to answer:

- which framework artifacts are exposed as managed dependencies
- whether a module belongs to epoch, plugins, or extension families
- whether a new module has been added to the managed dependency surface

## 4. Key Facts

- `zero-version` is the version manifest, not the runtime.
- `zero-version-epoch` owns managed exposure of `zero-epoch-*` and `zero-overlay`.
- `zero-version-plugins` owns managed exposure of capability plugins such as Redis, Excel, Flyway, monitor, security, Neo4j, email, SMS, and WebSocket.
- `zero-version-extension` owns managed exposure of `zero-extension-*` and `zero-exmodule-*`.

## 5. Source and Resource Path

Read in this order:

```text
zero-version-guide.md
-> zero-version/pom.xml
-> zero-version-epoch/pom.xml | zero-version-plugins/pom.xml | zero-version-extension/pom.xml | zero-version-overlay/pom.xml
-> exact module pom.xml only after the managed-exposure question is clear
```

High-value proof targets:

- parent `dependencyManagement`
- managed module declarations in each version family
- cross-family exposure of `zero-overlay`
- whether a new plugin/exmodule exists in source but is absent from version POMs

## 6. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for Zero-side managed dependency exposure
- `zero-ecotope` + `rachel-momo` when the unresolved point is parent/BOM interaction or shared version-governance layering above Zero
- `zero-ecotope` + `r2mo-rapid` only when a shared framework artifact must be proven on both runtime lines

## 7. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is usually not the default entry here because version ownership is POM-first.

Use direct graph retrieval only when:

- a module is already known,
- the unresolved point is which runtime or extension surfaces consume that module structurally,
- POM/resource proof still remains primary for exposure decisions.

## 8. AI Agent Rules

- Verify module exposure from the version POMs before assuming a module is part of the public framework surface.
- Do not infer dependency ownership only from directory names.
- When a new module appears in source but not in the version BOMs, treat the BOM update as an explicit framework task.
