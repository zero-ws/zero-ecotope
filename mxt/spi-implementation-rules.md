# SPI Implementation Rules

> Load this file when the task is about how Zero SPIs are implemented, registered, discovered, and evolved across exmodules and extension contracts.

## 1. Scope

This file owns:

- SPI implementation workflow
- SPI registration and discovery hotspots
- contract stability rules
- rules for adding new SPIs

It does not own:

- the full SPI family list
- CRUD-engine-first decisions
- unrelated plugin boot behavior

## 2. Runtime Loading Hotspots

These are the highest-value implementation hotspots:

1. `zero-extension-skeleton/.../boot/ExBoot.java`
2. `zero-extension-skeleton/.../spi/`
3. `META-INF/services/`
4. `HPI.findMany(...)`
5. exmodule `*-provider` modules

Rule:

```text
If the question is “how is this SPI loaded?”, inspect `spi/`, `META-INF/services`, and `HPI.findMany(...)` together.
```

## 3. Implementation Flow

The normal SPI lifecycle is:

1. define the interface in `zero-extension-skeleton/spi`
2. implement it in an exmodule `provider` module
3. register it through `META-INF/services`
4. let the framework discover it through `HPI.findMany(...)`
5. let boot wiring log or consume the loaded implementation set

## 4. Good SPI Use Cases

SPI-style modular extension is the right choice when:

- more than one business module may implement the same reusable contract
- runtime discovery is better than hardcoded wiring
- the behavior belongs above one single domain module
- frontend alignment may also be needed through `Ui*` contracts

Typical examples:

- account or tenant provisioning
- permission and resource routing
- dynamic UI composition
- modular configuration
- import/export hooks

## 5. Contract Stability Rules

- skeleton SPI interfaces are extension contracts, not per-project customization points
- exmodules may implement a contract, but should not redefine its shape for local convenience
- if one exmodule needs extra semantics, introduce an internal interface instead of mutating the shared SPI
- UI SPIs should stay aligned with `zero-ui/src/extension/`

## 6. When to Add a New SPI

Add a new SPI only when all of the following are true:

1. the behavior is reusable across more than one business module
2. the behavior belongs to the extension contract layer
3. existing `Ex*`, `Sc*`, or `Ui*` contracts cannot express it cleanly

If a new SPI is added:

- update `ExBoot.SPI_SET`
- update `spi-registry-map.md`
- update `search-hints.md`
- update `evolution-rules.md` if maintenance rules change

## 7. Agent Rules

- Do not add a new SPI for one exmodule-only convenience.
- Do not edit shared SPI contracts before proving the requirement is cross-module and reusable.
- If the change is only metadata-driven CRUD, do not create a new SPI; use `crud-engine-guide.md`.
