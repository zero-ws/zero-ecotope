# Extension Points

> Entry map for the Zero extension layer.
> Use this file first when the task says "extension", "SPI", "hook", "AOP", "CRUD engine", or "where is the reusable seam?"

## 1. Four Extension Families

Zero extension work is split into four families:

| Family | Owner document | Verified anchors |
|---|---|---|
| shared SPI contracts | `extension-skeleton-guide.md` | `ExBoot`, `SPI_SET`, `Ex*`, `Sc*`, `Ui*` |
| metadata-driven CRUD delivery | `extension-crud-guide.md` | `MDCRUDManager`, `Ix*`, `Pre*`, `Agonic*` |
| shared API-side extension conventions | `extension-api-guide.md` | `zero-extension-api` development/API support |
| AOP / before-after hook routing | `extension-aop-guide.md` | `zero-overlay/component/aop`, `AgonicAop`, `AspectPlugin` |

## 2. Use This Decision Table

| If the task is about... | Read first |
|---|---|
| SPI family ownership or `HPI.findMany(...)` | `extension-skeleton-guide.md` |
| `META-INF/services`, `Ex*`, `Sc*`, `Ui*` implementation | `spi-implementation-rules.md` |
| CRUD transport, standard routes, `entity.json`, `Pre*`, `Agonic*` | `extension-crud-guide.md` |
| actor/service code that should maybe be metadata-driven | `crud-engine-guide.md` |
| `Aspect`, `Before`, `After`, `Around`, AOP plugin, before/after execution | `extension-aop-guide.md` |
| one business module using the seam | matching `exmodule-*.md` guide |

## 3. What Counts As A Real Extension Point

A real extension point usually has all of these:

1. a shared contract or abstract seam
2. multiple implementations or module-specific realizations
3. runtime selection, registration, or hook execution

Typical evidence:

- `HPI.of(...)`
- `HPI.findMany(...)`
- `META-INF/services/*`
- `ExBoot.SPI_SET`
- reusable `Pre*` / `Agonic*` hook chains
- module-local before/after plugin contracts

If there is no shared contract and no runtime discovery, it is probably just ordinary module composition, not an extension point.

## 4. Anti-Patterns

- treating every helper class as an extension point
- editing one app actor before checking CRUD engine ownership
- inventing project-local hooks before checking `Ex*` / `Sc*` / `Ui*`
- confusing Zero overlay AOP with Spring AspectJ AOP

## 5. Practical Sequence

1. confirm whether the concern is SPI, CRUD, API-side support, or AOP
2. read the matching owner document
3. inspect source anchors
4. only then decide whether the change belongs in plugin, extension, exmodule, or app
