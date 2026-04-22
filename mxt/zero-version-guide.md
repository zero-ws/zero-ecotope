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

## 5. AI Agent Rules

- Verify module exposure from the version POMs before assuming a module is part of the public framework surface.
- Do not infer dependency ownership only from directory names.
- When a new module appears in source but not in the version BOMs, treat the BOM update as an explicit framework task.
