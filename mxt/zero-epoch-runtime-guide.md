# Zero Epoch Runtime Guide

> Load this file when the task is about the Zero core container, runtime lifecycle, request execution substrate, or the internal structure of `zero-epoch`.

## 1. Scope

This file owns:

- `zero-epoch` as the core runtime layer
- runtime sub-module roles
- container and execution substrate boundaries

It does not own plugin capability or exmodule business logic.

## 2. Owning Modules

| Module | Responsibility | When AI Agent Should Inspect |
|---|---|---|
| `zero-epoch-cosmic` | Vert.x container lifecycle, Verticle deployment, EventBus bridge | Container startup/shutdown failures, Verticle deployment errors |
| `zero-epoch-store` | DBE engine: `DBE`, jOOQ integration, `dslContext`, database access layer | Query failures, jOOQ DSL issues, DBE path errors |
| `zero-epoch-use` | Shared facade objects: `Ut`, `Ux`, `Fx` utility classes | Utility method behavior, facade contract questions |
| `zero-epoch-adhoc` | Ad-hoc request execution and dynamic dispatch | Dynamic endpoint routing, ad-hoc handler resolution |
| `zero-epoch-execution` | Standard request execution chain: Agent → Addr → Actor → Stub → Service | Request flow errors, address routing, execution chain breaks |
| `zero-epoch-focus` | Scheduling and job engine: `@Job`, `JobExtractor`, `JobStoreUnity` | Scheduled job failures, cron trigger issues, job lifecycle |
| `zero-epoch-setting` | Configuration loading: `vertx.yml`, `ConfigMod`, runtime config contracts | Config not loading, wrong config values, env-specific settings |
| `zero-overlay` | Bridge to `r2mo-rapid`: shared platform contracts, `EnvironmentVariable`, `EmService` | R2MO integration issues, cross-framework contract questions |
| `zero-epoch-spec` | Spec interface layer: shared spec contracts for config and integration | Spec contract mismatches, interface definition questions |
| `zero-epoch-spec-nacos` | Nacos config source: `NacosRule`, `YmSpec`, cloud config integration | Nacos connectivity, cloud config merge behavior |

## 3. AI Agent Rules

- Start here for container and execution questions before dropping to one sub-module.
- Use this file to classify whether the task belongs to runtime substrate, configuration, scheduling, storage, or shared platform contracts.

## 4. Runtime Reading Paths

### Container and execution flow

```text
zero-epoch-runtime-guide.md
-> zero-epoch-cosmic or zero-epoch-execution
-> exact source
```

### DBE or store path

```text
zero-epoch-runtime-guide.md
-> zero-epoch-store
-> dbe-query-rules.md if query shape matters
-> exact source/resources
```

### Config and environment path

```text
zero-epoch-runtime-guide.md
-> zero-epoch-setting or zero-epoch-spec-nacos
-> config-center-local-nacos.md
-> environment-contracts.md
-> exact config/source
```

### Job path

```text
zero-epoch-runtime-guide.md
-> zero-epoch-focus
-> job-model-guide.md
-> exact source
```

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for pure container/runtime questions
- `zero-ecotope` + `r2mo-rapid` when the issue is really in shared DBE/IO/contracts reused by the runtime
- `zero-ecotope` + `r2mo-spec` only when shared config/spec meaning is the unresolved point

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- a sub-module, class, or runtime seam is already known
- the unresolved point is structural spread inside `zero-epoch`
- a single graph query is cheaper than opening multiple sub-module trees

High-value targets:

- `ConfigMod`
- `DBSActor`
- `JobExtractor`
- `ZeroModule`
- EventBus or execution-chain anchors
