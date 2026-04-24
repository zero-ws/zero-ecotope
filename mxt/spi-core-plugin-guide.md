# SPI Core Plugin Guide

> Load this file when the task is about SPI-driven extension seams across the core plugin and extension layers, and the question is architectural rather than implementation-specific.

## 1. Scope

This file owns:

- SPI as the core extensibility model
- how plugins, extension skeleton, and exmodules meet at SPI boundaries
- when to switch from registry reading to implementation reading

It does not own one SPI family's implementation details.

## 2. Core Anchors

- `ExBoot`
- `SPI_SET`
- `HPI.findMany`
- `META-INF/services`
- `ConfigMod`
- `ExActivity`
- `ScPermit`
- `UiForm`

## 3. Source and Resource Path

Read in this order:

```text
spi-core-plugin-guide.md
-> spi-registry-map.md
-> spi-implementation-rules.md
-> exact SPI contract, provider source, and META-INF/services resources
```

High-value proof targets:

- `ExBoot`
- `SPI_SET`
- `HPI.findMany`
- `META-INF/services/*`
- representative SPI families such as `ConfigMod`, `ExActivity`, `ScPermit`, `UiForm`

## 4. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for Zero SPI ownership
- `zero-ecotope` + `r2mo-rapid` when the question crosses into shared IO/DBE/security capabilities reused on the Spring side
- `zero-ecotope` + `r2mo-spec` only when shared contract meaning is the unresolved point

## 5. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one SPI family or provider symbol is already known
- the unresolved point is structural spread between registry, provider, and consuming modules

## 6. AI Agent Rules

- Read this file first when the user says “core plugin layer via SPI”.
- Switch to `spi-registry-map.md` for family classification.
- Switch to `spi-implementation-rules.md` for implementation and registration details.
