# Zero Ecotope — MXT Knowledge Pack

> Final AI-oriented framework handbook for Zero downstream agents and framework contributors.
> Each document should own one question class, one owner concern, or one runtime topic.

## Document Index

| Document | Purpose |
|---|---|
| [framework-map.md](framework-map.md) | Full layer hierarchy, module tree, and runtime topology |
| [agent-quick-start.md](agent-quick-start.md) | One-page quick start for downstream AI agents reading Zero before loading the full framework rules |
| [abstraction-rules.md](abstraction-rules.md) | Three-letter objects, `Fx`/`Ux`/`Ut`, and code marker conventions |
| [plugin-layer-map.md](plugin-layer-map.md) | Capability plugins under `zero-plugins-equip` |
| [extension-points.md](extension-points.md) | Legacy SPI navigation page retained for stable references |
| [spi-registry-map.md](spi-registry-map.md) | Canonical SPI family map centered on `ExBoot.SPI_SET` |
| [spi-implementation-rules.md](spi-implementation-rules.md) | SPI implementation, registration, discovery, and stability rules |
| [exmodule-boundary.md](exmodule-boundary.md) | Exact boundary between plugin capability and business customization |
| [dual-side-development.md](dual-side-development.md) | Backend (Zero/R2MO) + frontend (Zero UI/R2MO Web) co-development guide |
| [backend-dpa-guide.md](backend-dpa-guide.md) | Zero backend DPA structure, async rules, and standard execution chain |
| [dbe-query-rules.md](dbe-query-rules.md) | DBE, `QQuery`, `QTree`, and query placement rules |
| [backend-model-tenant-rules.md](backend-model-tenant-rules.md) | Table conventions, model metadata, and tenant isolation rules |
| [backend-rbac-rules.md](backend-rbac-rules.md) | RBAC resource ownership and declarative permission rules |
| [crud-engine-guide.md](crud-engine-guide.md) | CRUD-engine-first decision rules for standard Zero modules |
| [contract-source-rules.md](contract-source-rules.md) | `.r2mo` contract authority and model-source precedence |
| [backend-api-integration.md](backend-api-integration.md) | Contract-aligned backend API integration into Zero runtime flow |
| [frontend-client-integration.md](frontend-client-integration.md) | Rust/Tauri/React client integration rules against shared contracts |
| [environment-contracts.md](environment-contracts.md) | Environment variables as runtime and integration contracts |
| [search-hints.md](search-hints.md) | Search patterns for fast codebase navigation |
| [graph-usage-rules.md](graph-usage-rules.md) | Operational rules for using `code-review-graph` safely in Zero framework work |
| [flyway-loading-flow.md](flyway-loading-flow.md) | Flyway actor startup, config assembly, and `DBFlyway` loading flow |
| [actor-startup-matrix.md](actor-startup-matrix.md) | `@Actor` startup matrix, sequencing, and config-gated boot semantics |
| [buildapp-buildperm-flow.md](buildapp-buildperm-flow.md) | Installation-time app/menu bootstrap and RBAC import flow |
| [oauth2-init-flow.md](oauth2-init-flow.md) | OAuth2 initialization flow and its dependency on Flyway bootstrap |
| [document-boundary-audit.md](document-boundary-audit.md) | SRP audit of the MXT pack and recommended future splits |
| [evolution-rules.md](evolution-rules.md) | How to keep this pack accurate after framework upgrades |
| [mcp-code-review-graph-rules.md](mcp-code-review-graph-rules.md) | Final framework-grade graph/navigation rules for downstream AI agents reading Zero across projects |


## Usage Model

Use the smallest document that matches the task.

Do not start with the largest file unless the task is already known to be broad.

| If the agent needs to... | Read this first | Then continue with |
|---|---|---|
| quickly decide where a problem belongs | `agent-quick-start.md` | `framework-map.md` |
| navigate framework code relationships | `agent-quick-start.md` | `mcp-code-review-graph-rules.md` |
| use graph tooling safely | `graph-usage-rules.md` | `mcp-code-review-graph-rules.md` |
| inspect Flyway or migration loading | `flyway-loading-flow.md` | `graph-usage-rules.md` |
| inspect `@Actor` startup ordering | `actor-startup-matrix.md` | `mcp-code-review-graph-rules.md` |
| inspect app/menu or RBAC bootstrap | `buildapp-buildperm-flow.md` | `search-hints.md` |
| inspect OAuth2 startup dependencies | `oauth2-init-flow.md` | `flyway-loading-flow.md` |
| decide plugin vs exmodule vs app ownership | `framework-map.md` | `exmodule-boundary.md` |
| inspect available SPI families | `spi-registry-map.md` | `spi-implementation-rules.md` |
| inspect SPI / extension implementation hooks | `spi-implementation-rules.md` | `mcp-code-review-graph-rules.md` |
| inspect backend DPA structure | `backend-dpa-guide.md` | `search-hints.md` |
| inspect DBE or query syntax | `dbe-query-rules.md` | `search-hints.md` |
| inspect model metadata or tenant rules | `backend-model-tenant-rules.md` | `crud-engine-guide.md` |
| inspect RBAC resource rules | `backend-rbac-rules.md` | `buildapp-buildperm-flow.md` |
| inspect standard CRUD delivery decisions | `crud-engine-guide.md` | `backend-dpa-guide.md` |
| inspect contract-source authority | `contract-source-rules.md` | `backend-api-integration.md` |
| inspect backend integration from contract to runtime | `backend-api-integration.md` | `dbe-query-rules.md` |
| inspect frontend client integration | `frontend-client-integration.md` | `contract-source-rules.md` |
| inspect environment-level contracts | `environment-contracts.md` | `framework-map.md` |
| understand project-local rule interaction | `project-rule-awareness.md` | project-local MDC / `CLAUDE.md` / `AGENTS.md` |

Default rule:

```text
Quick Start first, Full Protocol second, source verification last.
```

Final structure rule:

```text
This pack is finalized at the current document boundary.
Future updates should strengthen facts and anchors before creating new files.
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

## Baseline Order

1. `agent-quick-start.md`
2. `framework-map.md`
3. `exmodule-boundary.md`
4. `spi-registry-map.md`
5. `backend-dpa-guide.md`
6. `dbe-query-rules.md`
7. `crud-engine-guide.md`
8. `search-hints.md`
9. `graph-usage-rules.md`
10. `mcp-code-review-graph-rules.md`
11. `dual-side-development.md`
12. `evolution-rules.md`
