# Backend Development Guide

## Source Profile
- Framework-level Zero / Vert.x-first backend guidance
- Applies to downstream projects that inherit or import `io.zerows:zero-0216`
- Evidence base: Zero framework ownership, reusable backend layering, CRUD engine conventions, DBE/QQuery semantics, and verified downstream usage patterns

## 1. Backend Architecture Model

### DPA layering
Use a strict three-module backend split:

```text
<project>-domain   <- contracts, generated model, exceptions, resources
<project>-provider <- business logic, components, spi, boot
<project>-api      <- HTTP agents, event-bus actors, address constants
```

Dependency direction is fixed:

```text
Domain <- Provider <- API
```

Rules:
- Domain must not depend on provider or api.
- Provider depends on domain only.
- API depends on provider and exposes transport only.
- In Zero apps, this is the canonical business-module shape even when code is heavily generated.

### Golden Link
Implement request flow with the fixed chain:

```text
Agent -> Addr -> Actor -> Stub -> Service -> DBE
```

Meaning:
- `Agent`: HTTP entry declaration.
- `Addr`: event-bus address constant registry.
- `Actor`: event consumer that invokes stub.
- `Stub`: domain contract.
- `Service`: provider implementation of stub.
- `DBE`: DB/jOOQ DAO access layer.

## 2. Module Responsibilities

### Domain module
Typical responsibilities:
- generated jOOQ metadata / tables / records / daos
- `servicespec/` stub interfaces
- `exception/` error codes and typed exceptions
- shared enums/constants
- plugin resources under `src/main/resources/plugins/`

Resource ownership belongs here:
- `database/` DDL
- `flyway/` migrations
- `model/` metadata like `entity.json`, `column.json`
- `security/` RBAC resources
- `workflow/` BPMN / form config
- seed data and module config

### Provider module
Typical responsibilities:
- `serviceimpl/` business implementation
- `component/` reusable domain helpers
- `spi/` extension implementations
- `boot/` startup/config loading

Rules:
- service layer is the only place allowed to orchestrate DBE/DAO access and transactions
- business validation happens here before persistence
- tenant, audit, linkage, lifecycle hooks are coordinated here

### API module
Typical responsibilities:
- `Addr.java` for event-bus addresses
- `*Agent.java` for HTTP endpoint declarations
- `*Actor.java` for event consumer entry points

Rules:
- Agent contains no business logic.
- Actor contains no DB access.
- Actor injects stub and returns async result.
- Permission annotations and route metadata are attached at API level.

## 3. Async Programming Contract

Zero backend code is fully async.

Rules:
- All stub/service/actor methods return `Future<T>`.
- Do not block on futures.
- Use `compose`, `map`, `Future.failedFuture`, `Future.succeededFuture`.
- Do not place blocking I/O in agent/actor/service main flow.

## 4. Data Access Model

Zero business modules use DBE-style consistency with DB / jOOQ DAO execution paths.

### Main styles
1. DBE query object style via `QQuery`
2. Direct DAO/DB async access via `DB.on(SomeDao.class)` or generated DAO classes

### QQuery structure
Use a normalized request envelope:

```json
{
  "criteria": {},
  "pager": { "page": 1, "size": 20 },
  "sorter": ["createdAt,DESC"],
  "projection": ["id", "name"]
}
```

Responsibilities:
- `criteria`: where clause tree
- `pager`: pagination
- `sorter`: order by
- `projection`: selected fields

### QTree criteria rules
Direct node format:

```text
"field,op": value
```

Important operators:
- default / `,=` equal
- `,<>` not equal
- `,>` `,>=` `,<` `,<=`
- `,i` / `,!i` in / not in
- `,n` / `,!n` is null / is not null
- `,s` starts with
- `,e` ends with
- `,c` contains

Logic rules:
- AND is default
- OR uses `"": false`
- nested logical groups use `$group`-style keys

Example:

```json
{
  "": false,
  "name": "Lang",
  "$1": {
    "": true,
    "status": "ACTIVE",
    "age,>=": 18
  }
}
```

### Query placement rules
- Build complex query composition in service layer.
- Keep pager/sorter/projection outside `criteria`.
- Use `projection` for large or sensitive fields.
- Ensure indexed fields back common filters and sorters.

## 5. Table and Model Conventions

### Table prefixes
- `X_` business entity tables
- `R_` relation tables
- `S_` system/security tables
- `O_` other/supporting tables

### Required common fields
Business tables typically carry:
- `key`
- `sigma`
- `appId` when app-level isolation is needed
- `language`
- `active`
- `createdBy`
- `createdAt`
- `updatedBy`
- `updatedAt`
- `deleted`

Rules:
- `sigma` is mandatory for tenant-safe business data.
- Add index on `SIGMA`.
- Unique constraints should include tenant field where appropriate.
- Logical delete is preferred over physical delete.

### Model metadata
Keep entity metadata in domain resources, usually under:

```text
plugins/{module}/model/{identifier}/entity.json
plugins/{module}/model/{identifier}/column.json
```

Common uses:
- unique constraint declaration
- default initialization values
- UI/search/sort rendering metadata
- field rendering and filter config

## 6. Multi-Tenant Rules

Zero app modules are shared-db/shared-schema multi-tenant by default.

Core context values:
- `Habitus.sigma()` tenant
- `Habitus.appId()` application
- `Habitus.userId()` user
- `Habitus.language()` locale

Request headers mapped by framework:
- `X-Sigma`
- `X-App-Id`
- `X-Lang`
- `Authorization`

Rules:
- framework auto-injects tenant/audit fields on insert
- framework auto-adds tenant filter in DBE/DB queries
- do not manually set `sigma` unless implementing special cross-tenant logic
- cross-tenant operations require elevated permission checks
- row-level isolation config belongs in security/model config, not ad hoc query code

## 7. Permission and RBAC Rules

Zero permission chain is:

```text
User -> Group -> Role -> Resource -> Action -> API
```

### Resource ownership
Permission resources live in domain resources:

```text
plugins/{module}/security/RBAC_RESOURCE/
plugins/{module}/security/RBAC_ROLE/
plugins/{module}/security/RBAC_ADMIN/
```

### API mapping
At API/openapi level, map endpoints to `{resource, action}`.
At code level, annotate agents with `@Permission`.

Typical actions:
- `create`
- `read`
- `search`
- `update`
- `delete`
- `import`
- `export`

### Data permission
Use `seekSyntax.json` for row filters.
Use `dmConfig.json` for field-level permissions.

`seekSyntax.json` uses DBE criteria syntax and placeholders such as:
- `${sigma}`
- `${userId}`
- `${orgId}`
- `${viewId}`

Rule:
- prefer declarative row filtering in RBAC resources over hand-coded permission predicates in services.

## 8. SPI Extension Model

Zero business modules extend behavior through SPI.

Categories:
- `Ex*` business extension points
- `Sc*` security extension points
- `Ui*` UI extension points

Common SPIs:
- `ExAtom`
- `ExApp`
- `ExInit`
- `ExIo`
- `ExLinkage`
- `ExModulat`
- `ExTodo`
- `ExTransit`
- `ScPermit`

Implementation rules:
- place implementations in provider `spi/`
- register in `META-INF/services/`
- configure module `configuration.json` if needed
- methods stay async and return `Future<T>`
- throw failures via `Future.failedFuture()`

Use SPI for:
- lifecycle interception
- initialization
- attachment/file behavior
- permission overrides
- workflow/todo/linkage hooks
- module-specific extension behavior without polluting core service flow

## 9. Workflow as Resource-Driven Backend Feature

Workflow modules are resource/config-driven.

Assets usually live under:

```text
plugins/{module}/workflow/
```

Typical contents:
- `running/{processId}/workflow.bpmn`
- task forms and json config
- linkage config
- excel definitions

Rules:
- BPMN and form assets belong to domain resources.
- backend stubs/services expose workflow actions; definitions remain declarative.
- use SPI hooks such as `ExTodo` and `ExLinkage` for lifecycle integration.

## 10. Specification Sources of Truth

This project family uses mixed model-driven inputs. Backend generation and implementation should align with:
- `.r2mo/domain/*.proto`
- `.r2mo/api/operations/{uri}/*.md`
- `.r2mo/api/components/schemas/*`
- openapi metadata, including permission extensions
- generated Java domain/jOOQ outputs

Rule:
- spec artifacts define contract first; handwritten backend code conforms to those contracts.
- when app code is sparse or generated, read MDC + `.r2mo` + generated bootstrap classes together.

## 11. Zero CRUD Engine First Rule

Zero includes a built-in CRUD engine. For standard interface scenarios, the default decision should be zero-code or low-code first, not handwritten DPA first.

### CRUD engine coverage
The CRUD engine already exposes standardized transport contracts for common module operations under `/api` with `{actor}` routing.

Covered capabilities include:
- create: `POST /api/{actor}`
- search: `POST /api/{actor}/search`
- existing / missing validation: `POST /api/{actor}/existing`, `POST /api/{actor}/missing`
- fetch by id: `GET /api/{actor}/{key}`
- fetch all by tenant: `GET /api/{actor}/by/sigma`
- update by id: `PUT /api/{actor}/{key}`
- batch update: `PUT /batch/{actor}/update`
- delete by id: `DELETE /api/{actor}/{key}`
- batch delete: `DELETE /batch/{actor}/delete`
- import / export: `POST /api/{actor}/import`, `POST /api/{actor}/export`
- view/column metadata: `GET /columns/{actor}/full`, `GET /columns/{actor}/my`, `PUT /columns/{actor}/my`

Practical implication:
- standard CRUD modules do not need a handwritten Agent/Actor/Stub/Service stack just to expose ordinary create/read/update/delete/search/import/export endpoints
- the engine already implements the transport layer and delegates through metadata-driven runtime execution

### `entity.json` as metadata entrypoint
For CRUD-engine modules, `plugins/{module}/model/{identifier}/entity.json` is the primary backend entrypoint.

It defines core metadata such as:
- model `name`
- `daoCls` binding to DAO/table model
- `field.unique` unique-key strategy
- `transform.initial` default field initialization
- business tag / categorization

In practice, the CRUD engine uses model identifier and module metadata to:
- bind actor to module
- infer identifier defaults
- initialize column metadata
- initialize audit/header defaults
- drive unique-key lookup and standard persistence behavior

Rule:
- if a module fits CRUD engine shape, start from `entity.json` and related model resources before designing handwritten services

### Agent decision rule
Before writing backend code for a Zero module, evaluate in this order:
1. Can CRUD engine cover the endpoint with existing standardized routes and model metadata?
2. Can the requirement be solved by `entity.json`, `column.json`, RBAC resources, workflow resources, or SPI hooks?
3. Only if the answer is no, fall back to handwritten DPA implementation.

### Zero-code / low-code cases
Prefer CRUD engine when the requirement is mainly:
- standard single-entity CRUD
- tenant-safe search/list/detail pages
- batch update or batch delete
- uniqueness validation
- import/export around tabular data
- dynamic column/view metadata delivery
- audit/default-field injection
- standard attachment/audit/tree/reference preprocessing already handled by engine pipeline

This is zero-code or low-code because the main work is metadata and resource configuration, not handwritten Java business flow.

### Must-handwrite cases
Use handwritten DPA when the requirement needs behavior beyond standardized CRUD engine semantics, for example:
- cross-aggregate orchestration across multiple bounded contexts
- custom transaction flow not representable as engine preprocessing/AOP hooks
- bespoke event choreography or nonstandard transport contract
- domain-specific state machine logic that is not just workflow/resource configuration
- security/business rules that cannot be expressed through RBAC resources, model metadata, or SPI extension points
- algorithms or external integrations whose core value is imperative logic rather than metadata-driven persistence

### Relationship to DPA
DPA remains the architectural baseline for Zero business modules, but CRUD engine is the preferred implementation path for standard interface scenarios.

Decision rule:
- standard module interface -> CRUD engine first
- nonstandard domain logic -> handwritten DPA

## 12. Minimal Implementation Checklist for a New Zero Backend Module

1. Create DPA modules in root `pom.xml`.
2. In domain:
   - define DDL / flyway
   - generate or register tables/dao artifacts
   - add stub interfaces
   - add ERR / typed exceptions
   - add model/security/workflow resources as needed
3. In provider:
   - implement stub in `serviceimpl/`
   - add components/spi hooks
   - keep transactions and business validation here
4. In api:
   - define `Addr`
   - add `Actor`
   - add `Agent`
   - bind permission metadata
5. Ensure async `Future<T>` end to end.
6. Use QQuery / DBE / DB DAO paths instead of ad hoc SQL wiring.
7. Preserve tenant/audit/resource-driven conventions.

## 13. Evidence

### Project evidence
- `r2mo-apps-admin/pom.xml`: root project uses parent `io.zerows:zero-0216` and declares `r2mo-apps-admin-domain`, `r2mo-apps-admin-provider`, `r2mo-apps-admin-api`.
- `r2mo-apps-admin-domain/src/main/java/io/zerows/apps/zero/admin/ModuleADMINGeneration.java`: domain participates in generated metadata model via `MetaGenerate`.
- `r2mo-apps-admin-provider/src/main/java/io/zerows/apps/zero/admin/ModuleADMINSource.java`: provider bootstraps with `ExtensionLauncher` and generation config.

### Rule evidence
- `.cursor/rules/r2-backend-dbe.mdc`: defines DPA dependency order and Golden Link.
- `.cursor/rules/r2-backend-zero.mdc`: defines Zero DBE query syntax and QTree operators.
- `.cursor/rules/r2-backend-zero-api.mdc`: defines Agent/Actor/Stub/Service responsibilities, async contract, DB paths, transactions.
- `.cursor/rules/r2-backend-zero-crud.mdc`: defines table prefixes, common fields, model resources, logical delete, tenant-aware CRUD.
- `.cursor/rules/r2-backend-zero-tenant.mdc`: defines `sigma`-based isolation and Habitus context.
- `.cursor/rules/r2-backend-zero-permission.mdc`: defines RBAC resources, `@Permission`, `seekSyntax.json`, field-level permission config.
- `.cursor/rules/r2-backend-zero-spi.mdc`: defines SPI categories, registration, and provider-side extension patterns.
- `.cursor/rules/r2-backend-zero-workflow.mdc`: defines workflow resource layout and SPI integration.
