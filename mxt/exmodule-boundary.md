# ExModule Boundary

> This document defines the most important boundary in Zero Ecotope: the boundary between **plugin capability** and **business customization**.

## 1. Boundary Definition

| Layer | What it solves | What should not live here |
|---|---|---|
| `zero-plugins-equip` | Reusable infrastructure capability | Business rules, domain models, project tables |
| `zero-extension-skeleton` | Shared extension contracts | Concrete domain implementation |
| `zero-extension-crud` | Reusable CRUD scaffolding | Domain-specific behavior |
| `zero-extension-api` | External API conventions and routing base | Concrete business handling |
| `zero-exmodule-*` | Reusable domain modules such as RBAC/Finance/Workflow | Single-project one-off customization |
| Application layer | Project-specific business customization | Re-implementation of standard exmodule capability |

## 2. Runtime Boundary Summary

Use this one-line runtime split when placing code:
- **core** (`zero-epoch`, `zero-boot`) owns runtime execution
- **plugin** (`zero-plugins-equip`) owns reusable capability
- **extension** (`zero-extension-skeleton`, `zero-extension-crud`, `zero-extension-api`) owns reusable contracts and scaffolding
- **exmodule** (`zero-exmodule-*`) owns reusable domain meaning
- **application** owns final project-specific behavior

This is the shortest correct mental model for agents.

## 3. What Belongs in Each Place

### Put it in `zero-plugins-equip` when it is:
- a third-party adapter
- protocol integration
- infrastructure capability with no business meaning
- reusable across unrelated domains

Examples:
- Redis integration
- Elasticsearch integration
- WebSocket transport
- LDAP or OAuth2 authentication protocol support

### Put it in `zero-extension-skeleton` when it is:
- a reusable extension contract
- a cross-domain SPI definition
- boot-time extension discovery logic

Examples:
- `ExTenantProvision`
- `ScPermit`
- `UiForm`

### Put it in `zero-exmodule-*` when it is:
- a reusable domain implementation
- a domain-specific SPI implementation
- a standard business capability used by multiple projects

Examples:
- RBAC role/permission logic
- workflow state rules
- finance domain APIs

### Put it in the application layer when it is:
- unique to one project
- tied to one customer's process
- an override of a standard exmodule behavior for local needs

## 4. When Business Rules Do *Not* Belong in the Plugin Layer

Do **not** put logic in `zero-plugins-equip` if it answers any of these questions:
- which role can access this action?
- when should a notification be sent?
- how should a workflow transition be validated?
- which cache key semantics belong to a specific domain?
- how should a project-specific import/export rule work?

These are business or domain concerns. They belong in exmodules or the application layer.

Plugin layer code should stay at the level of:
- protocol support
- connection and client handling
- capability exposure
- transport or storage adapters

## 5. Common Mistakes

### Mistake 1: writing business rules in a plugin
Wrong:
- defining business cache-key semantics inside a Redis plugin

Correct:
- define the business rule in an exmodule or app layer, then call the plugin through framework abstractions

### Mistake 2: bypassing plugin abstractions
Wrong:
- exmodule code depending directly on a plugin implementation class

Correct:
- exmodule code uses `Fx`, `Ux`, or SPI discovery through `HPI`

### Mistake 3: mutating a skeleton SPI for one exmodule
Wrong:
- changing `ExTenantProvision` only because one exmodule wants a custom field

Correct:
- keep the shared contract stable and handle extra semantics inside the exmodule itself

### Mistake 4: re-implementing exmodule capability in the app layer
Wrong:
- app code rebuilding RBAC permission logic from scratch

Correct:
- reuse `zero-exmodule-rbac` and extend it where needed

## 6. Internal ExModule Structure

Most exmodules follow this structure:

```text
zero-exmodule-{name}/
├── zero-exmodule-{name}-api/
├── zero-exmodule-{name}-domain/
└── zero-exmodule-{name}-provider/
```

Rules:
- `*-api` is the outward-facing dependency surface.
- `*-domain` contains shared domain language, models, or stubs.
- `*-provider` contains internal implementation and SPI wiring.
- Other modules should depend on `*-api`, not `*-provider`.

## 7. One-Line Rule

**Plugins provide capability. Extensions provide contracts. Exmodules provide reusable business meaning. Applications provide project-specific customization.**
