# Extension Points

> `zero-extension-skeleton` defines the SPI contracts that business exmodules implement.
> These extension points are centrally registered by `ExBoot.SPI_SET` and discovered through `HPI.findMany(...)`.

## 1. Registered SPI Groups

Source of truth: `io.zerows.extension.skeleton.boot.ExBoot`

### Framework Integration SPIs

| SPI | Responsibility |
|---|---|
| `ConfigMod` | Module-level configuration extension |
| `HQBE` | High-order query builder extension |
| `Dictionary` | Dictionary data SPI |

### Business Extension SPIs

| SPI | Responsibility |
|---|---|
| `ExActivity` | Activity tracking and audit extension |
| `ExApp` | Application-level extension logic |
| `ExArbor` | Tree-structure extension |
| `ExAtom` | Atomic metadata/model extension |
| `ExAttachment` | Attachment management extension |
| `ExAccountProvision` | Account provisioning extension |
| `ExIo` | Import/export extension |
| `ExLinkage` | Linkage/relation extension |
| `ExModulat` | Modular configuration extension |
| `ExOwner` | Owner/tenant assignment logic |
| `ExSetting` | System/settings extension |
| `ExTenantProvision` | Tenant provisioning during registration |
| `ExTransit` | State transition extension |
| `ExUser` | User-model extension |

### Security SPIs

| SPI | Responsibility |
|---|---|
| `ScCredential` | Credential handling and validation |
| `ScOrbit` | Permission/resource routing model |
| `ScPermit` | Permission-rule evaluation |
| `ScRoutine` | Standard security routine extension |
| `ScSeeker` | Permission/resource discovery |

### UI SPIs

| SPI | Responsibility |
|---|---|
| `UiApeak` | Top-bar UI extension |
| `UiApeakMy` | Personal top-bar UI extension |
| `UiForm` | Dynamic form extension |
| `UiValve` | UI visibility / valve control |

## 2. Plugin Registration and Extension Loading Hotspots

These are the highest-value hotspots for agent navigation and MCP extraction:

1. `zero-extension-skeleton/.../boot/ExBoot.java`
   - the canonical registration list via `SPI_SET`
   - the fastest way to understand which SPI-like extension points exist
2. `zero-extension-skeleton/.../spi/`
   - the contract directory for `Ex*`, `Sc*`, and `Ui*` interfaces
3. `META-INF/services/`
   - the Java SPI registration point for provider implementations
4. `HPI.findMany(...)`
   - the runtime discovery mechanism used to load implementations
5. exmodule `*-provider` modules
   - the most common implementation hotspot for SPI-like modular extension

If an agent needs to understand how Zero loads extensions, these are the first locations to inspect.

## 3. How These SPIs Are Used

1. The interface is defined in `zero-extension-skeleton/spi`.
2. An exmodule `provider` module implements the SPI.
3. The implementation is registered through Java SPI (`META-INF/services`).
4. The framework discovers implementations through `HPI.findMany(...)`.
5. `ExBoot` logs the loaded implementation list at startup.

Example contract:

```java
public interface ExTenantProvision {
    Future<JsonObject> provision(JsonObject input);
}
```

## 4. CRUD Engine as a Primary Extension Point

`zero-extension-crud` is one of the most important Zero extension points because it turns standard business interfaces into metadata-driven runtime behavior.

### Why it matters
- it provides built-in REST/event-bus coverage for standard CRUD, search, batch update/delete, import/export, and column-view endpoints
- it shifts backend work from handwritten transport/service code to model/resource configuration
- it makes `entity.json` and related model metadata first-class extension inputs
- it lets agents prefer zero-code or low-code delivery before choosing handwritten DPA

### Agent rule
When a Zero requirement looks like a standard data-management interface, inspect CRUD engine coverage first.
Only choose handwritten DPA after confirming that CRUD engine metadata + SPI/resource extensions cannot express the requirement cleanly.

## 5. Where SPI-like Modular Extension Is Most Useful

SPI-like modular extension is the right tool when:
- multiple exmodules may implement the same reusable contract
- the framework needs runtime discovery instead of hardcoded wiring
- the behavior is cross-domain and should stay above any single business module
- the extension may also need frontend alignment through `Ui*` contracts

Typical hotspots:
- account/tenant provisioning
- permission and resource routing
- dynamic UI composition
- modular configuration or import/export hooks

## 6. Contract Stability Rules

- Skeleton SPI interfaces are extension contracts, not per-project customization points.
- Exmodules may implement a contract, but should not redefine the contract shape for their own convenience.
- If a single exmodule needs additional semantics, it should introduce its own internal interface instead of mutating the shared skeleton contract.
- UI SPIs should stay aligned with `zero-ui/src/extension/` so backend and frontend extensions remain compatible.

## 7. When to Add a New SPI

Add a new SPI only when all of the following are true:
1. The behavior is reusable across more than one business module.
2. The behavior belongs to the extension contract layer, not to one domain module.
3. Existing `Ex*`, `Sc*`, or `Ui*` contracts cannot express it cleanly.

If a new SPI is added:
- update `ExBoot.SPI_SET`
- update this file
- update `search-hints.md`
- update `evolution-rules.md` if the change affects upgrade rules
