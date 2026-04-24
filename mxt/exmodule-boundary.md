# ExModule Boundary

> Use this file when the question is "should this live in plugin, extension, exmodule, or the current app?"

## 1. Core Boundary

Zero Ecotope has five different ownership layers. They are not interchangeable.

| Layer | Owns | Does not own |
|---|---|---|
| `zero-epoch`, `zero-boot` | runtime execution, boot assembly, request/event pipeline | business semantics, project-specific rules |
| `zero-plugins-equip` | reusable infrastructure capability, protocol adapters, runtime integrations | domain policy, reusable business meaning |
| `zero-extension-skeleton` | extension contracts, SPI vocabulary, shared discovery surface | module-specific implementation |
| `zero-extension-crud`, `zero-extension-api` | reusable delivery pipeline and API-side extension conventions | one domain's service logic |
| `zero-exmodule-*` | reusable business modules and module-owned resources | one project's private customization |

One-line rule:

```text
Plugins provide capability, extensions define and route reusable seams, exmodules carry reusable business meaning, applications finish local customization.
```

## 2. Verified Runtime Split

This repository shows the split directly in source:

- extension contracts are centralized in `zero-extension-skeleton/src/main/java/.../spi/*`
- CRUD delivery is centralized in `zero-extension-crud` through `MDCRUDManager`, `Ix*`, `Pre*`, `Agonic*`
- exmodules follow the repeated shape:
  - `*-api`
  - `*-domain`
  - `*-provider`
  - optional `*-ui`
- module boot is owned by `MD*Actor` and `Extension*Source`

That means ownership is structural, not just naming preference.

## 3. Placement Rules

### Put code in `zero-plugins-equip` when it is:

- a third-party integration
- a protocol adapter
- storage / cache / monitor / websocket / OAuth2 capability
- reusable with no domain meaning of its own

Examples:

- Redis-backed cache
- Flyway runtime execution
- Excel file parsing engine
- WebSocket transport

### Put code in `zero-extension-skeleton` when it is:

- a new SPI contract
- shared extension vocabulary
- discovery / registry behavior needed by many modules
- `HPI.findMany(...)`-facing contract ownership

Examples verified in source:

- `ExAttachment`
- `ExModulat`
- `ExActivity`
- `ScPermit`
- `ScCredential`
- `UiForm`
- `UiValve`

### Put code in `zero-extension-crud` when it is:

- metadata-driven CRUD transport
- reusable request preprocessing
- reusable create / update / delete / import / view pipeline behavior
- UCA chain behavior that multiple modules share

Examples verified in source:

- `MDCRUDManager`
- `IxSetupModule`
- `PreAudit*`, `PreFile*`, `PreExcel`
- `AgonicADBCreate`, `AgonicADBUpdate`, `AgonicADBDelete`, `AgonicADBImport`
- `DocExtensionActor`

### Put code in `zero-extension-api` when it is:

- reusable API-side convention
- common transport-facing helper surface
- shared extension API support, not one module's actor logic

### Put code in `zero-exmodule-*` when it is:

- reusable business module logic
- domain-specific SPI implementation
- module-owned Flyway / model / RBAC / logging / modulat resources
- business behavior shared by more than one application

Examples:

- `zero-exmodule-rbac` authorization semantics
- `zero-exmodule-workflow` workflow/todo/business process coordination
- `zero-exmodule-ambient` app registry, attachment metadata, activity history
- `zero-exmodule-modulat` bag/block modular configuration

### Put code in the current app when it is:

- customer-specific
- tenant-local and not intended for reuse
- an override of reusable exmodule behavior for one deployment

## 4. Internal Exmodule Shape

Most exmodules use the same four-way split:

```text
zero-exmodule-{name}/
├── zero-exmodule-{name}-api/
├── zero-exmodule-{name}-domain/
├── zero-exmodule-{name}-provider/
└── zero-exmodule-{name}-ui/    # optional
```

Rules:

- `*-api` exposes `Actor` / `Agent` / `Addr`
- `*-domain` owns tables, generated assets, `servicespec`, and resource truth
- `*-provider` owns `Service`, `MD*Actor`, `Extension*Source`, SPI implementations
- `*-ui` may own backend-fed UI resources, but not provider/service logic

## 5. Resource Ownership Is Part Of The Module

For Zero exmodules, behavior is often controlled by both Java and resources. Treat these as implementation, not decoration:

- `plugins/{MID}.yml`
- `plugins/{MID}/flyway/**`
- `plugins/{MID}/model/**`
- `plugins/{MID}/security/RBAC_RESOURCE/**`
- `plugins/{MID}/security/RBAC_ROLE/**`
- `plugins/{MID}/logging/logback-segment.xml`
- `modulat/**`
- `META-INF/services/*`

If a task changes module behavior, inspect these before writing controller/service workarounds.

## 6. AOP Boundary

Zero has more than one "AOP-like" mechanism. Do not collapse them.

| Mechanism | Owner |
|---|---|
| generic framework AOP pipeline | `zero-overlay/src/main/java/io/zerows/component/aop/*` |
| CRUD before/after execution around standard mutations | `zero-extension-crud` |
| module-local business before/after hooks | the matching exmodule provider |
| modeling plugin before/after routing | `zero-exmodule-mbsecore` |

Use `extension-aop-guide.md` when the task explicitly mentions AOP, before/after plugins, `Around`, `Aspect`, or module-local hook chains.

## 7. Common Mistakes

### Mistake 1: business rules in plugin modules

Wrong:

- role/resource semantics inside Redis/OAuth2/email plugins

Correct:

- keep protocol/storage in plugin, move business authorization/notification semantics to the exmodule

### Mistake 2: inventing a new app-local seam before checking SPI

Wrong:

- hardcoding a custom service switch in one application

Correct:

- inspect `zero-extension-skeleton` SPI and existing `META-INF/services`

### Mistake 3: patching one actor when the rule belongs to CRUD pipeline

Wrong:

- duplicating validation/pre-audit/file handling in a single actor

Correct:

- change `zero-extension-crud` when the behavior is standard and reusable

### Mistake 4: changing skeleton contracts for one module's local need

Wrong:

- mutating shared SPI because one exmodule wants a private field

Correct:

- keep the SPI stable and encode module-specific semantics in the provider/resource layer

## 8. Fast Decision Sequence

1. Is the behavior generic infrastructure?
   Then start in `zero-plugins-equip`.
2. Is it a reusable contract or discovery surface?
   Then start in `zero-extension-skeleton`.
3. Is it standard CRUD delivery or metadata-driven transport?
   Then start in `zero-extension-crud`.
4. Is it reusable domain/business meaning?
   Then start in the matching `zero-exmodule-*`.
5. Is it truly local?
   Then keep it in the current application.
