# Actor Startup Matrix

> Load this file when the task is about `@Actor` startup semantics, startup order, config gating, or modular boot execution.

## 1. Scope

This file explains how Zero turns `@Actor` classes into a startup matrix.

It is not about:

- EventBus request handlers
- `@Address` routing pairs
- Flyway internals
- RBAC/app bootstrap payloads

## 2. `@Actor` Contract

`@Actor` is a boot-time module contract, not only a marker annotation.

Important fields:

- `value` — config key used for actor config lookup
- `sequence` — startup bucket and phase ordering
- `configured` — whether missing config means skip

Sequence meaning:

- `< 0` = built-in / prerequisite actors
- `-1` = default actor order from the annotation itself
- `>= 0` = extension and business-layer actors

One-line rule:

```text
`@Actor` is a boot contract. Sequence and config rules are part of runtime semantics, not decoration.
```

## 3. Discovery Flow

Class discovery begins in `ORepositoryClass`.

Actor scanning is delegated to `InquirerClassActor`.

The actual runtime matrix is built in:

```text
ZeroModule.initGlobalMatrix()
```

## 4. Matrix Construction

The startup matrix is built as follows:

1. read cached classes
2. keep only classes implementing `HActor`
3. keep only classes annotated with `@Actor`
4. instantiate singleton actor instances
5. group them by `annotation.sequence()`
6. sort sequence buckets in ascending order

## 5. Phase Split

Startup phases are split by `ZeroLauncher`:

- `ZeroLauncher.Pre` starts `sequence < 0`
- `ZeroLauncher.Mod` starts `sequence >= 0`

Actor boot is intentionally a two-phase matrix. Do not flatten it conceptually.

## 6. Runtime Rules

- Sequence buckets execute in ascending order.
- Actors inside the same sequence bucket are dispatched in parallel.
- Config lookup uses `annotation.value()`.
- Config resolution order is:
  - `NodeStore.findInfix(...)`
  - `NodeStore.findExtension(...)`
- If `configured = true` and config is missing, the actor is skipped.
- If `configured = false`, the actor may start without config.
- `PrimedActor` is the verified late-stage module actor with `sequence = Short.MAX_VALUE` and `configured = false`.

## 7. Execution Wrapper

`AbstractHActor` wraps concrete startup in:

```text
Vertx.executeBlocking(...)
  -> protected startAsync(HConfig, Vertx)
```

Interpretation:

- framework wrapper owns blocking-safe startup dispatch
- concrete actors own their own initialization logic

Verified source anchors:

- `ZeroModule.initGlobalMatrix()` builds and sorts the global actor matrix.
- `ZeroLauncher.Pre.waitAsync(...)` runs the negative-sequence actor phase.
- `ZeroLauncher.Mod.waitAsync(...)` runs the non-negative actor phase.

## 8. Agent Rules

- Do not reason about actor startup from annotation values alone; verify `ZeroLauncher` and `ZeroModule`.
- Do not assume all actors require config.
- Do not flatten all actors into one startup phase.
- When startup order matters, compare `sequence` first, then phase, then the actor implementation itself.
- When an actor appears to be “missing”, verify both class discovery and config gating before changing code.
