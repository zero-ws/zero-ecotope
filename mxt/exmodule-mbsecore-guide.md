# Exmodule MBSE Core Guide

> Load this file when the task is about dynamic modeling core, data plugins, or modeling before/after plugin routing in `zero-exmodule-mbsecore`.

## 1. Scope

`zero-exmodule-mbsecore` owns reusable modeling-core business behavior.

It is the right entry when the task is about:

- model/entity/relation metadata behavior,
- modeling-side plugin orchestration,
- model-aware before/after hooks,
- module-owned generated schema resources,
- MBSE-specific data service flow.

## 2. Verified Anchors

- Domain:
  - `DataStub`
- Provider:
  - `DataService`
  - `MDMBSECoreActor`
  - `ExtensionMBSECoreSource`
- API/plugin contracts:
  - `AspectPlugin`
  - `BeforePlugin`
  - `AfterPlugin`
  - `DataPlugin`
- Provider/plugin routing:
  - `IoArranger`

Additional repository anchors worth checking directly:

- `zero-exmodule-mbsecore-domain/src/main/java/.../domain/*`
- `zero-exmodule-mbsecore-domain/src/main/resources/plugins/zero-exmodule-mbsecore.yml`
- `zero-exmodule-mbsecore-domain/src/main/resources/flyway.conf`
- `zero-exmodule-mbsecore-provider/src/main/resources/vertx-generate.yml`
- `META-INF/services` registrations under the provider module

## 3. AOP / Plugin Boundary

This exmodule owns modeling-specific before/after plugin routing.

Use it when the task is about:

- modeling plugin ordering
- before/after data plugin execution
- `AspectPlugin`-style modeling hooks

## 4. Boundary

Do not use MBSE Core for:

- generic overlay AOP
- generic CRUD AOP
- datasource boot ownership

Also do not use it for:

- generic DBE criteria/pager/sorter semantics,
- raw jOOQ runtime wiring,
- non-modeling integration transport,
- pure BOM/version governance.

## 5. Source and Resource Path

Read in this order:

```text
exmodule-mbsecore-guide.md
-> zero-exmodule-mbsecore-api for plugin contracts
-> zero-exmodule-mbsecore-domain for model/schema semantics
-> zero-exmodule-mbsecore-provider for runtime boot and plugin arrangement
-> shared DBE or jOOQ docs only if persistence-layer ownership is still unresolved
```

High-value proof targets:

- `DataStub`
- `DataService`
- `AspectPlugin`
- `BeforePlugin`
- `AfterPlugin`
- `IoArranger`
- `MDMBSECoreActor`
- `ExtensionMBSECoreSource`
- module-owned `plugins/*.yml` and `META-INF/services/*`

## 6. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for modeling-core behavior
- `zero-ecotope` + `r2mo-rapid` when the unresolved point crosses into DBE, jOOQ generation, or shared persistence abstractions
- `zero-ecotope` + `r2mo-spec` only when model metadata exposure becomes a shared contract question

Do not pull in `rachel-momo` unless dependency or plugin-governance surface is the real problem.

## 7. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one modeling symbol is already known,
- the unresolved point is structural spread between API contract, domain model resources, and provider-side routing,
- source/resource proof is still read afterward as final evidence.
