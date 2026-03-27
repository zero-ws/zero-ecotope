# Framework Map

## 1. Root Structure

The root `pom.xml` declares `zero-ecotope` and splits the repository into three top-level parts:

- `zero-version` — BOM and version locking
- `zero-0216` — shared dependency baseline
- `zero-ecosystem` — the actual framework runtime ecosystem

Inside `zero-ecosystem/pom.xml`, the runtime expands into four major module groups:

```text
zero-ecosystem/
├── zero-epoch             # Core runtime
├── zero-boot              # Boot wiring
├── zero-plugins-equip     # Capability plugins
└── zero-plugins-extension # Extension contracts + exmodules
```

## 2. Runtime Layer Order

```text
Application / Business Project
        ↓
zero-plugins-extension
        ↓
zero-plugins-equip
        ↓
zero-boot
        ↓
zero-epoch
```

Interpretation:
- `zero-epoch` is the runtime substrate.
- `zero-boot` assembles startup and module wiring.
- `zero-plugins-equip` adds pluggable infrastructure capability.
- `zero-plugins-extension` turns those capabilities into reusable business-facing extension contracts and exmodules.
- the application layer applies final project-specific customization.

## 3. Layer Responsibilities

### zero-epoch — Core Runtime

Owns the Zero runtime itself:
- Vert.x-based container lifecycle
- EventBus and request pipeline
- DBE (Database Engine)
- local and remote configuration loading
- runtime utility abstractions such as `Fx`, `Ux`, `Ut`, `HPI`, `DBE`, `HED`, `HOI`, `HFS/RFS`

Representative sub-modules:
- `zero-epoch-cosmic`
- `zero-epoch-store`
- `zero-epoch-use`
- `zero-epoch-adhoc`
- `zero-epoch-execution`
- `zero-epoch-focus`
- `zero-epoch-setting`
- `zero-overlay`

This is the Zero-first layer. It is Vert.x-oriented, async-friendly, and optimized for interaction-heavy runtime paths.

Important clarification for agents:
- Zero is not "async-only" or "EventBus-only".
- In management-heavy systems, the database layer is also a first-class path.
- Zero still carries a DBE-style unified data-access syntax, but real project implementations often land in DB classes, repository-like DB helpers, and jOOQ DAO paths.
- When reading exmodule and extension code, expect both runtime/event orchestration and standard database access to coexist.

### zero-boot — Boot Wiring

Responsible for startup assembly:
- instance loading
- module discovery
- extension layer boot hookup
- `HActor`-style launcher integration

Rule: this layer wires modules together but should not carry domain logic.

### zero-plugins-equip — Capability Plugins

Provides reusable, pluggable infrastructure capability:
- cache
- search
- session
- messaging
- monitoring
- security protocols

This layer answers: **what capability can the framework plug in?**
It does not answer: **how a business module should use that capability.**

### zero-plugins-extension — Extension Layer

Contains two sub-levels.

**Framework skeleton level**
- `zero-extension-skeleton`
- `zero-extension-crud`
- `zero-extension-api`

**Business exmodule level**
- `zero-exmodule-rbac`
- `zero-exmodule-finance`
- `zero-exmodule-workflow`
- `zero-exmodule-ui`
- `zero-exmodule-ambient`
- `zero-exmodule-report`
- `zero-exmodule-erp`
- `zero-exmodule-lbs`
- `zero-exmodule-graphic`
- `zero-exmodule-integration`
- `zero-exmodule-tpl`
- `zero-exmodule-modulat`
- `zero-exmodule-mbseapi`
- `zero-exmodule-mbsecore`

This layer answers: **which business behaviors are reusable across projects?**

### zero-ui — Frontend Scaffold

`zero-ui` is the React scaffold aligned with Zero Extension contracts.
Its `src/extension/` subtree mirrors backend extension structure so frontend and backend can evolve together.

### zero-version — Version Manifest

This module centralizes ecosystem version locking. `zero-version-extension/pom.xml` is the quickest source for the exmodule artifact list.

## 4. Three Framework Relationships

The ecosystem is easiest to understand as three stacked frameworks:

1. **Zero runtime layer** — Vert.x-first runtime and execution model (`zero-epoch`, `zero-boot`)
2. **R2MO / Zero shared standardization layer** — common conventions, contracts, and normalized semantics
3. **Application extension layer** — reusable exmodules plus project customization

Important clarification:
- `r2mo-spec` should be treated as an **upstream semantic contract layer** from the Zero side.
- In practice, this means Zero exmodules may depend on shared naming, resource, or semantic conventions defined upstream, even when the runtime stays Vert.x-first.
- Zero owns runtime behavior; `r2mo-spec`-style contracts standardize semantics above runtime.

## 5. Scenario Split: Zero vs R2MO

| Stack | Primary runtime style | Best-fit scenario |
|---|---|---|
| Zero | Vert.x-first, EventBus-first, async/concurrent | compute-heavy, interaction-heavy, event-driven flows; also valid for management systems that still want standardized DB access |
| R2MO | Spring-first, management-first | CRUD/admin/workflow/configuration-heavy systems |

So the split is not “old vs new” or “backend A vs backend B”. The split is **scenario-driven**:
- choose **Zero** when runtime interaction model matters most
- choose **R2MO** when management structure and Spring-oriented delivery matter most
- also keep in mind that Zero remains viable for management-heavy systems when teams want Vert.x runtime behavior plus consistent DBE/DB access patterns

## 6. Environment Variables as Architectural Contracts

In Zero Ecotope, environment variables are **part of the framework architecture**, not incidental deployment configuration.

This distinction matters because Zero supports multiple runtime modes simultaneously:
- multi-tenant (one deployment serving multiple tenants)
- multi-language (locale-driven content selection)
- multi-style (configurable UI or API presentation style)
- multi-application (multiple logical apps sharing the same runtime)

In these modes, environment variables drive the runtime contract. They determine:
- which tenant context is active at startup
- which plugin modules are loaded (e.g., security protocol selection, cache backend)
- which extension SPIs are expected to be present
- which application-level configuration is effective

Agent rule:
- When an agent encounters a Zero configuration problem, the correct first step is to inspect the env/runtime contract (environment variables, `vertx.yml`, configuration module), not to modify a business exmodule or plugin.
- Changing a plugin or exmodule to work around a missing env/runtime value is a category error.
- Configuration problems belong to the configuration layer. Business modules should not compensate for absent env-level setup.

Search anchors for env/runtime contract:
- `vertx.yml`
- `ConfigMod`
- `zero-epoch-setting`
- `TENANT_ID`, `APP_ID`, `APP_KEY`, `LANG`, `STYLE`
- environment variable loading in `zero-epoch-cosmic`

## 7. Structural Conclusions

1. `zero-epoch` defines the core runtime boundary.
2. `zero-plugins-equip` defines the infrastructure capability boundary.
3. `zero-plugins-extension` defines the reusable business extension boundary.
4. `r2mo-spec`-style standards act as upstream semantic contracts, not as Zero runtime owners.
5. R2MO and Zero are scenario-specific runtimes layered over shared extension concepts.
6. Environment variables are framework-level architectural contracts, not deployment side-notes.
