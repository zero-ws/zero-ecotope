# Zero Extension Skeleton Guide

> Load this file when the task is about `zero-extension-skeleton`, shared SPI contracts, `ExBoot`, or deciding whether a reusable seam should be introduced at the contract layer.

## 1. Scope

`zero-extension-skeleton` owns the shared extension contract layer.

It owns:

- SPI contracts under `.../spi/*`
- extension boot registration through `ExBoot`
- framework-visible SPI registry assembly through `SPI_SET`
- shared extension vocabulary reused by exmodules and plugins

It does not own:

- one exmodule's provider logic
- module-specific resource trees
- CRUD execution behavior
- app-level customization

## 2. Verified Anchors

Confirmed source anchors:

- `zero-extension-skeleton/.../boot/ExBoot.java`
- `zero-extension-skeleton/.../boot/ExID.java`
- `zero-extension-skeleton/.../underway/PrimedActor.java`
- `zero-extension-skeleton/.../common/KeIpc.java`
- `zero-extension-skeleton/.../spi/ExActivity.java`
- `zero-extension-skeleton/.../spi/ExAttachment.java`
- `zero-extension-skeleton/.../spi/ExApp.java`
- `zero-extension-skeleton/.../spi/ExAtom.java`
- `zero-extension-skeleton/.../spi/ExIo.java`
- `zero-extension-skeleton/.../spi/ExModulat.java`
- `zero-extension-skeleton/.../spi/ExTodo.java`
- `zero-extension-skeleton/.../spi/ScCredential.java`
- `zero-extension-skeleton/.../spi/ScPermit.java`
- `zero-extension-skeleton/.../spi/ScRoutine.java`
- `zero-extension-skeleton/.../spi/ScSeeker.java`
- `zero-extension-skeleton/.../spi/UiApeak.java`
- `zero-extension-skeleton/.../spi/UiApeakMy.java`
- `zero-extension-skeleton/.../spi/UiForm.java`
- `zero-extension-skeleton/.../spi/UiValve.java`

## 3. What `ExBoot` Really Owns

`ExBoot` is not business logic. It is the extension registry surface.

Verified behavior:

- `SPI_SET` enumerates the shared SPI families
- boot scans implementations through `HPI.findMany(...)`
- extension bundles are collected through framework boot/runtime metadata

Interpretation:

- if a new reusable seam must be visible across modules, the contract should appear here first
- the implementation should still live in plugin or exmodule code

## 4. SPI Family Groups

### Business/module-side contracts

- `ExActivity`
- `ExApp`
- `ExAttachment`
- `ExInit`
- `ExLinkage`
- `ExModulat`
- `ExSetting`
- `ExTodo`
- `ExTransit`
- `ExUser`

### Security/authorization-side contracts

- `ScCredential`
- `ScConfine`
- `ScModeling`
- `ScOrbit`
- `ScPermit`
- `ScRoutine`
- `ScSeeker`

### UI-side contracts

- `UiApeak`
- `UiApeakMy`
- `UiAnchoret`
- `UiForm`
- `UiValve`

## 5. Boundary Rules

Put behavior in `zero-extension-skeleton` when it is:

- a reusable SPI contract
- shared extension vocabulary
- framework boot/discovery logic
- a seam required by multiple exmodules or plugins

Do not put behavior here when it is:

- module-specific service logic
- one exmodule's rules/resources
- CRUD engine behavior
- AOP execution details of one pipeline

## 6. Working Rule

Before inventing a new hook:

1. inspect existing `Ex*`, `Sc*`, `Ui*`
2. inspect `ExBoot.SPI_SET`
3. inspect existing `META-INF/services`
4. only then add a new shared contract

That order avoids contract sprawl.

## 7. Companion Documents

- `extension-crud-guide.md` for CRUD delivery hooks
- `extension-aop-guide.md` for AOP/before-after routing
- `spi-registry-map.md` for the canonical SPI inventory
- `spi-implementation-rules.md` for implementation and registration mechanics
