# Zero Ecotope — MXT Knowledge Pack

> **AI-first knowledge pack.** This documentation set is optimized for AI agents and framework contributors who need to identify the correct layer, boundary rule, and extension point immediately.
> Each document owns one concern. Read them in the order listed below.

## Document Index

| Document | Purpose |
|---|---|
| [framework-map.md](framework-map.md) | Full layer hierarchy, module tree, and runtime topology |
| [abstraction-rules.md](abstraction-rules.md) | Three-letter objects, `Fx`/`Ux`/`Ut`, and code marker conventions |
| [plugin-layer-map.md](plugin-layer-map.md) | Capability plugins under `zero-plugins-equip` |
| [extension-points.md](extension-points.md) | SPI contracts registered in `ExBoot.SPI_SET` |
| [exmodule-boundary.md](exmodule-boundary.md) | Exact boundary between plugin capability and business customization |
| [dual-side-development.md](dual-side-development.md) | Backend (Zero/R2MO) + frontend (Zero UI/R2MO Web) co-development guide |
| [search-hints.md](search-hints.md) | Search patterns for fast codebase navigation |
| [evolution-rules.md](evolution-rules.md) | How to keep this pack accurate after framework upgrades |

## Scenario Map

From the repository README:

| Scenario | Backend | Frontend |
|---|---|---|
| Management-intensive | R2MO Rapid (Spring Boot) | Zero UI |
| Compute / interaction-intensive | Zero Epoch (Vert.x) | R2MO Web |

Key facts:
- Both scenarios share `zero-plugins-extension` as the business extension layer.
- `zero-epoch` owns the Vert.x runtime, EventBus path, and DBE path.
- R2MO reuses the same extension contracts but routes through Spring MVC.
- `zero-ui` is the preferred frontend scaffold for management-intensive projects.

## Quick Layer Summary

- `zero-epoch` — Zero Core: container lifecycle, config, DBE, request/event execution.
- `zero-boot` — boot wiring between core runtime and extension layer.
- `zero-plugins-equip` — reusable infrastructure capability plugins.
- `zero-plugins-extension` — extension contracts plus reusable business exmodules.
- `zero-ui` — React scaffold aligned with Zero Extension contracts.

## Recommended Reading Order

1. `framework-map.md`
2. `exmodule-boundary.md`
3. `extension-points.md`
4. `search-hints.md`
5. `dual-side-development.md`
6. `evolution-rules.md`
