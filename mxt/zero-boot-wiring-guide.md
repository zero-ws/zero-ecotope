# Zero Boot Wiring Guide

> Load this file when the task is about `zero-boot`, boot wiring, launcher entrypoints, extension boot tools, or installation-time boot modules.

## 1. Scope

This file owns `zero-boot` as the boot wiring layer.

It owns:

- launcher-to-runtime boot wiring
- boot actor module grouping
- extension boot utilities
- installation and app/RBAC import boot helpers
- cloud boot integration points

It does not own:

- `@Actor` startup matrix semantics
- one plugin actor's internal startup logic
- one exmodule's business behavior

## 2. Owning Modules

- `zero-boot`
- `zero-boot-epoch-actor`
- `zero-boot-cloud-actor`
- `zero-boot-actor`
- `zero-boot-extension`
- `zero-boot-inst`
- `zero-boot-import`

Verified graph/source anchors:

- `VertxApplication`
- `LauncherApp`
- `CloudActor`
- `BuildApp`
- `BuildPerm`
- `LoadApp`
- `MenuApp`

## 3. Sub-Module Responsibilities

| Module | Responsibility | When AI Agent Should Inspect |
|---|---|---|
| `zero-boot` | Parent POM and shared boot infrastructure | Boot module inclusion or version issues |
| `zero-boot-actor` | Generic actor boot utilities and shared actor lifecycle helpers | Actor lifecycle wiring issues that are not specific to epoch or cloud |
| `zero-boot-epoch-actor` | Zero runtime actor boot: wires `@Actor`-annotated classes into the Vertx container during startup | `@Actor` not registering, startup ordering, actor missing from container |
| `zero-boot-cloud-actor` | Cloud/Nacos actor boot: wires cloud config source actors (`ConfigLoadCloud`, `NacosRule`) | Cloud config not loading, Nacos connection failures, config source priority |
| `zero-boot-extension` | Extension channel and component wiring: connects `zero-extension-skeleton` SPI boot to runtime | Extension SPI not discovered, `ExBoot` not firing, `SPI_SET` incomplete |
| `zero-boot-inst` | Installation boot: `BuildApp`, `BuildPerm`, app/menu/RBAC data import at first launch | App/menu missing after fresh install, RBAC resource import failures |
| `zero-boot-import` | Import boot helpers: shared import utilities for bulk data loading during installation | Bulk import errors, data seeding failures |

One-line rule:

```text
`zero-boot` wires framework startup; `zero-epoch` owns runtime execution.
```

## 4. Boundary Rules

Use `zero-boot-wiring-guide.md` when the task starts from boot module placement or launcher wiring.

Use:

- `actor-startup-matrix.md` for actor sequencing rules
- `buildapp-buildperm-flow.md` for app/menu/RBAC import flow
- `config-center-local-nacos.md` for local/cloud config loading
- `zero-epoch-runtime-guide.md` for runtime execution internals

## 5. AI Agent Rules

- Do not treat `zero-boot` as a business layer.
- Do not debug plugin actor behavior from boot wiring alone.
- If a startup problem crosses launcher, cloud config, and actors, identify the owner phase first.
- For installation data issues, inspect `BuildApp` / `BuildPerm` before changing exmodule providers.
