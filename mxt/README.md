# Zero Ecotope — MXT Knowledge Pack

> **AI-first knowledge pack.** This documentation set is optimized for AI agents and framework contributors who need to identify the correct layer, boundary rule, and extension point immediately.
> Each document owns one concern. Read them in the order listed below.

## Document Index

| Document | Purpose |
|---|---|
| [framework-map.md](framework-map.md) | Full layer hierarchy, module tree, and runtime topology |
| [agent-quick-start.md](agent-quick-start.md) | One-page quick start for downstream AI agents reading Zero before loading the full framework rules |
| [abstraction-rules.md](abstraction-rules.md) | Three-letter objects, `Fx`/`Ux`/`Ut`, and code marker conventions |
| [plugin-layer-map.md](plugin-layer-map.md) | Capability plugins under `zero-plugins-equip` |
| [extension-points.md](extension-points.md) | SPI contracts registered in `ExBoot.SPI_SET` |
| [exmodule-boundary.md](exmodule-boundary.md) | Exact boundary between plugin capability and business customization |
| [dual-side-development.md](dual-side-development.md) | Backend (Zero/R2MO) + frontend (Zero UI/R2MO Web) co-development guide |
| [search-hints.md](search-hints.md) | Search patterns for fast codebase navigation |
| [evolution-rules.md](evolution-rules.md) | How to keep this pack accurate after framework upgrades |
| [mcp-code-review-graph-rules.md](mcp-code-review-graph-rules.md) | Final framework-grade graph/navigation rules for downstream AI agents reading Zero across projects |


## How To Use This Pack

Choose the smallest entry that matches the task:

| If the agent needs to... | Read this first | Then continue with |
|---|---|---|
| quickly decide where a problem belongs | `agent-quick-start.md` | `framework-map.md` |
| navigate framework code relationships | `agent-quick-start.md` | `mcp-code-review-graph-rules.md` |
| decide plugin vs exmodule vs app ownership | `framework-map.md` | `exmodule-boundary.md` |
| inspect SPI / extension hooks | `extension-points.md` | `mcp-code-review-graph-rules.md` |
| inspect CRUD / DBE / DPA behavior | `backend-dev-guide.md` | `search-hints.md` |
| understand project-local rule interaction | `project-rule-awareness.md` | project-local MDC / `CLAUDE.md` / `AGENTS.md` |

Default rule for downstream AI agents:

```text
Quick Start first, Full Protocol second, source verification last.
```

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

1. `agent-quick-start.md`
2. `framework-map.md`
3. `exmodule-boundary.md`
4. `extension-points.md`
5. `search-hints.md`
6. `mcp-code-review-graph-rules.md`
7. `dual-side-development.md`
8. `evolution-rules.md`
