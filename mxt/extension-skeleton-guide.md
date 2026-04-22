# Zero Extension Skeleton Guide

> Load this file when the task is about `zero-extension-skeleton`, shared extension contracts, SPI boot registration, or the boundary between skeleton contracts and concrete exmodule implementations.

## 1. Scope

This file owns `zero-extension-skeleton`.

It owns:

- shared extension SPI contracts
- `ExBoot` registration responsibility
- `SPI_SET` as the skeleton-level SPI registry entry
- the boundary between skeleton contracts and concrete exmodule implementations

It does not own:

- one SPI family's detailed implementation rules
- one exmodule's business behavior
- generic plugin capability behavior

## 2. Owning Module

- `zero-plugins-extension/zero-extension-skeleton`

Verified source anchors:

- `zero-extension-skeleton/.../boot/ExBoot.java`
- `zero-extension-skeleton/.../spi/`
- `io.zerows.extension.skeleton.boot.ExBoot`
- `io.zerows.extension.skeleton.spi.*`

## 3. Responsibility Model

`zero-extension-skeleton` is the contract layer between Zero core extensibility and reusable business modules.

It defines:

- the SPI interfaces exmodules and plugins implement
- the shared discovery surface consumed by `HPI.findMany(...)`
- the stable extension vocabulary used across `Ex*`, `Sc*`, and `Ui*`

It should not hold:

- module-specific provider logic
- concrete business workflows
- app-level customization

One-line rule:

```text
`zero-extension-skeleton` defines extension contracts; exmodules realize them.
```

## 4. `ExBoot` and `SPI_SET`

`ExBoot` is the skeleton boot entry that assembles the extension-side SPI registry.

Verified behavior from `ExBoot.java`:

- `SPI_SET` explicitly registers the shared SPI families
- boot logging verifies discovered implementations through `HPI.findMany(...)`
- module logging inspects `HMaven` implementors and collects bundle IDs

High-value registered SPI families include:

- `ExActivity`
- `ExApp`
- `ExAttachment`
- `ExModulat`
- `ExSetting`
- `ExTransit`
- `ExUser`
- `ScCredential`
- `ScPermit`
- `ScRoutine`
- `ScSeeker`
- `UiForm`
- `UiApeak`
- `UiApeakMy`
- `UiValve`

Interpretation:

- `SPI_SET` is not business metadata; it is the skeleton-owned registry surface
- if a reusable SPI must be visible framework-wide, its contract belongs here first
- the implementation still belongs in the matching plugin or exmodule

## 5. Boundary Rules

Put behavior in `zero-extension-skeleton` when it is:

- a reusable SPI contract
- a shared extension-side vocabulary
- a framework-level boot registration concern
- required by multiple exmodules or plugins

Do not put behavior here when it is:

- one domain module's provider implementation
- RBAC resource content
- CRUD metadata behavior
- UI/business module logic specific to one exmodule

## 6. AI Agent Rules

- Read this file first when the user names `zero-extension-skeleton` explicitly.
- Switch to `spi-registry-map.md` when the task is about SPI family classification.
- Switch to `spi-implementation-rules.md` when the task is about registration files or implementation mechanics.
- Switch to a concrete exmodule guide when the question becomes business-domain specific.
