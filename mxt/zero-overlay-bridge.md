# Zero Overlay Bridge

> Load this file when the task is about `zero-overlay`, environment bridge contracts, platform enums, or the Zero side of integration with `r2mo-rapid`.

## 1. Scope

This file owns:

- `zero-overlay` positioning in the framework
- platform and environment constants exposed upward
- bridge-facing enums and shared constants
- why `zero-overlay` is part of epoch-level exposure

It does not own Spring-side behavior.

## 2. Owning Modules

- `zero-ecosystem/zero-epoch/zero-overlay`
- `zero-version-epoch`

## 3. Key Anchors

- `io.zerows.platform.EnvironmentVariable`
- `io.zerows.platform.enums.EmService`
- `io.zerows.platform.constant.VName`
- `io.zerows.component.aop.*`

## 4. Bridge Model

`zero-overlay` is the Zero-side bridge layer.
It exposes shared platform contracts used by:

- environment handling
- service enums such as job type and status
- common constants needed across runtime and extension modules

It should be read as a reusable contract layer between Zero runtime internals and upper consumers, including integration with `r2mo-rapid`.

## 5. AI Agent Rules

- Do not treat `zero-overlay` as an application module.
- When a task touches shared environment names, service enums, or bridge constants, inspect `zero-overlay` before editing higher layers.
- Keep Spring-side assumptions out of this file; it is the Zero-facing bridge definition.
