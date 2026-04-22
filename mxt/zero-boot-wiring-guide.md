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

## 3. Responsibility Model

`zero-boot` is the wiring layer between core runtime, boot actors, and extension installation helpers.

It connects:

- runtime launch entrypoints
- cloud configuration boot
- extension channel and component wiring
- installation-time app/menu/RBAC loading
- optional boot actor families for Elasticsearch and graph behavior

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
