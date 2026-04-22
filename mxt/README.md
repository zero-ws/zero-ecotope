# Zero Ecotope — MXT Knowledge Pack

> Final AI-oriented framework handbook for Zero downstream agents and framework contributors.
> Each document should own one question class, one owner concern, or one runtime topic.

## Document Index

| Document | Purpose |
|---|---|
| [framework-map.md](framework-map.md) | Full layer hierarchy, module tree, and runtime topology |
| [zero-epoch-runtime-guide.md](zero-epoch-runtime-guide.md) | Dedicated owner map for the Zero core runtime layer and its sub-modules |
| [zero-boot-wiring-guide.md](zero-boot-wiring-guide.md) | Dedicated owner map for `zero-boot` launch, cloud, extension, and installation wiring |
| [agent-quick-start.md](agent-quick-start.md) | One-page quick start for downstream AI agents reading Zero before loading the full framework rules |
| [mcp-integration-map.md](mcp-integration-map.md) | Topic-to-module routing map for MCP-assisted framework reading across Zero core, plugins, and exmodules |
| [mcp-fast-retrieval-rules.md](mcp-fast-retrieval-rules.md) | Shortest-path MCP retrieval discipline for AI Agents |
| [distillation-rules.md](distillation-rules.md) | Rules for compressing MXT documents into agent-readable anchors and decisions |
| [purification-rules.md](purification-rules.md) | Rules for deduplicating and assigning one authoritative owner per rule |
| [zero-version-guide.md](zero-version-guide.md) | BOM split, managed dependency exposure, and version-surface ownership |
| [abstraction-rules.md](abstraction-rules.md) | Three-letter objects, `Fx`/`Ux`/`Ut`, and code marker conventions |
| [spi-core-plugin-guide.md](spi-core-plugin-guide.md) | Core SPI-driven extension model across plugins, skeleton, and exmodules |
| [config-center-local-nacos.md](config-center-local-nacos.md) | Local plus Nacos config-center loading and merge anchors |
| [dbs-multi-datasource.md](dbs-multi-datasource.md) | Static and dynamic data-source registration through `DBSActor` |
| [zero-overlay-bridge.md](zero-overlay-bridge.md) | Zero-facing bridge contracts and integration-facing overlay ownership |
| [job-model-guide.md](job-model-guide.md) | `@Job`, mission extraction, and MBSE job-facing surfaces |
| [plugin-layer-map.md](plugin-layer-map.md) | Capability plugins under `zero-plugins-equip` |
| [cache-redis-guide.md](cache-redis-guide.md) | Cache capability split and Redis startup ownership |
| [elasticsearch-guide.md](elasticsearch-guide.md) | Elasticsearch plugin capability, client, and indexer ownership |
| [excel-import-export-guide.md](excel-import-export-guide.md) | Excel plugin startup, import/export anchors, and environment config |
| [monitor-center-guide.md](monitor-center-guide.md) | Monitor plugin family and monitor startup ownership |
| [security-plugin-flow.md](security-plugin-flow.md) | Security plugin family, provider composition, and protocol actor anchors |
| [session-guide.md](session-guide.md) | Session plugin startup, session store, and handler capability ownership |
| [oauth2-capability-guide.md](oauth2-capability-guide.md) | OAuth2 plugin capability and registered-client ownership |
| [neo4j-guide.md](neo4j-guide.md) | Neo4j plugin capability and client ownership |
| [swagger-openapi-guide.md](swagger-openapi-guide.md) | Swagger/OpenAPI documentation plugin capability ownership |
| [trash-capability-guide.md](trash-capability-guide.md) | Trash/recycle-bin plugin capability ownership |
| [email-capability-guide.md](email-capability-guide.md) | Email delivery capability vs email-auth boundary |
| [sms-capability-guide.md](sms-capability-guide.md) | SMS delivery capability vs SMS-auth boundary |
| [weco-capability-guide.md](weco-capability-guide.md) | Weco platform capability vs Weco security boundary |
| [websocket-guide.md](websocket-guide.md) | WebSocket and active-push capability ownership |
| [extension-points.md](extension-points.md) | Legacy SPI navigation page retained for stable references |
| [extension-skeleton-guide.md](extension-skeleton-guide.md) | Dedicated owner map for `zero-extension-skeleton` and its shared SPI contracts |
| [spi-registry-map.md](spi-registry-map.md) | Canonical SPI family map centered on `ExBoot.SPI_SET` |
| [spi-implementation-rules.md](spi-implementation-rules.md) | SPI implementation, registration, discovery, and stability rules |
| [exmodule-boundary.md](exmodule-boundary.md) | Exact boundary between plugin capability and business customization |
| [extension-api-guide.md](extension-api-guide.md) | `zero-extension-api` positioning and reusable API-side extension responsibility |
| [exmodule-ambient-guide.md](exmodule-ambient-guide.md) | Ambient reusable business ownership for app registry, activity, attachment, and init logic |
| [exmodule-erp-guide.md](exmodule-erp-guide.md) | ERP reusable organization and employee module ownership |
| [exmodule-finance-guide.md](exmodule-finance-guide.md) | Finance reusable processing and settlement module ownership |
| [exmodule-graphic-guide.md](exmodule-graphic-guide.md) | Graphic engine business module ownership |
| [exmodule-integration-guide.md](exmodule-integration-guide.md) | Reusable integration directory and message module ownership |
| [exmodule-lbs-guide.md](exmodule-lbs-guide.md) | LBS business module ownership |
| [exmodule-mbseapi-guide.md](exmodule-mbseapi-guide.md) | Dynamic API and job-facing MBSE module ownership |
| [exmodule-mbsecore-guide.md](exmodule-mbsecore-guide.md) | Dynamic modeling core module ownership |
| [exmodule-modulat-guide.md](exmodule-modulat-guide.md) | Modular configuration service ownership |
| [exmodule-rbac-guide.md](exmodule-rbac-guide.md) | Reusable RBAC module ownership and its boundary vs security plugins |
| [exmodule-report-guide.md](exmodule-report-guide.md) | Configurable report-center ownership |
| [exmodule-tpl-guide.md](exmodule-tpl-guide.md) | Template-center and personal-setting ownership |
| [exmodule-ui-guide.md](exmodule-ui-guide.md) | UI configuration center ownership |
| [exmodule-workflow-guide.md](exmodule-workflow-guide.md) | Workflow engine business ownership |
| [attachment-storage-integration-guide.md](attachment-storage-integration-guide.md) | Attachment upload plus storage/integration ownership |
| [static-modeling-guide.md](static-modeling-guide.md) | Static modeling chain across CRUD, UI, and MBSE core |
| [activity-log-guide.md](activity-log-guide.md) | Ambient-owned activity logging and activity-rule ownership |
| [report-center-guide.md](report-center-guide.md) | Configurable report-center ownership |
| [acl-authorization-guide.md](acl-authorization-guide.md) | ACL and reinforced authorization ownership above raw auth protocols |
| [modulat-ui-unified-guide.md](modulat-ui-unified-guide.md) | Modular configuration service together with unified UI handling |
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
| inspect the Zero runtime layer itself | `zero-epoch-runtime-guide.md` | `framework-map.md` |
| inspect boot wiring and launch integration | `zero-boot-wiring-guide.md` | `actor-startup-matrix.md` |
| route a broad framework topic into modules and graph targets | `mcp-integration-map.md` | `graph-usage-rules.md` |
| consume the pack with minimum MCP tokens | `mcp-fast-retrieval-rules.md` | `mcp-integration-map.md` |
| compress or rewrite verbose docs | `distillation-rules.md` | owner document |
| remove duplicate or mixed rules | `purification-rules.md` | true owner document |
| inspect BOM and managed dependency exposure | `zero-version-guide.md` | `framework-map.md` |
| inspect local plus Nacos config loading | `config-center-local-nacos.md` | `environment-contracts.md` |
| inspect multi-datasource registration | `dbs-multi-datasource.md` | `dbe-query-rules.md` |
| inspect overlay bridge contracts | `zero-overlay-bridge.md` | `framework-map.md` |
| inspect core SPI-driven plugin architecture | `spi-core-plugin-guide.md` | `spi-registry-map.md` |
| inspect scheduled job model | `job-model-guide.md` | `mcp-code-review-graph-rules.md` |
| navigate framework code relationships | `agent-quick-start.md` | `mcp-code-review-graph-rules.md` |
| use graph tooling safely | `graph-usage-rules.md` | `mcp-code-review-graph-rules.md` |
| inspect cache and Redis capability | `cache-redis-guide.md` | `plugin-layer-map.md` |
| inspect Elasticsearch capability | `elasticsearch-guide.md` | `plugin-layer-map.md` |
| inspect Excel import and export | `excel-import-export-guide.md` | `plugin-layer-map.md` |
| inspect Flyway or migration loading | `flyway-loading-flow.md` | `graph-usage-rules.md` |
| inspect monitor plugin ownership | `monitor-center-guide.md` | `plugin-layer-map.md` |
| inspect security plugin flow | `security-plugin-flow.md` | `backend-rbac-rules.md` |
| inspect session capability | `session-guide.md` | `security-plugin-flow.md` |
| inspect OAuth2 plugin capability | `oauth2-capability-guide.md` | `oauth2-init-flow.md` |
| inspect Neo4j capability | `neo4j-guide.md` | `plugin-layer-map.md` |
| inspect Swagger/OpenAPI documentation | `swagger-openapi-guide.md` | `plugin-layer-map.md` |
| inspect trash/recycle-bin capability | `trash-capability-guide.md` | `plugin-layer-map.md` |
| inspect email delivery capability | `email-capability-guide.md` | `security-plugin-flow.md` |
| inspect SMS delivery capability | `sms-capability-guide.md` | `security-plugin-flow.md` |
| inspect Weco capability | `weco-capability-guide.md` | `security-plugin-flow.md` |
| inspect WebSocket capability | `websocket-guide.md` | `plugin-layer-map.md` |
| inspect `@Actor` startup ordering | `actor-startup-matrix.md` | `mcp-code-review-graph-rules.md` |
| inspect app/menu or RBAC bootstrap | `buildapp-buildperm-flow.md` | `search-hints.md` |
| inspect OAuth2 startup dependencies | `oauth2-init-flow.md` | `flyway-loading-flow.md` |
| decide plugin vs exmodule vs app ownership | `framework-map.md` | `exmodule-boundary.md` |
| inspect `zero-extension-skeleton` ownership | `extension-skeleton-guide.md` | `spi-registry-map.md` |
| inspect available SPI families | `spi-registry-map.md` | `spi-implementation-rules.md` |
| inspect SPI / extension implementation hooks | `spi-implementation-rules.md` | `mcp-code-review-graph-rules.md` |
| inspect `zero-extension-api` ownership | `extension-api-guide.md` | `exmodule-boundary.md` |
| inspect ambient reusable business behavior | `exmodule-ambient-guide.md` | `backend-rbac-rules.md` |
| inspect ERP reusable business behavior | `exmodule-erp-guide.md` | `backend-dpa-guide.md` |
| inspect finance reusable business behavior | `exmodule-finance-guide.md` | `backend-dpa-guide.md` |
| inspect graphic reusable business behavior | `exmodule-graphic-guide.md` | `neo4j-guide.md` |
| inspect integration reusable business behavior | `exmodule-integration-guide.md` | `backend-api-integration.md` |
| inspect LBS reusable business behavior | `exmodule-lbs-guide.md` | `backend-dpa-guide.md` |
| inspect MBSE API reusable behavior | `exmodule-mbseapi-guide.md` | `job-model-guide.md` |
| inspect MBSE core reusable behavior | `exmodule-mbsecore-guide.md` | `dbe-query-rules.md` |
| inspect modular config business behavior | `exmodule-modulat-guide.md` | `exmodule-ui-guide.md` |
| inspect RBAC reusable business behavior | `exmodule-rbac-guide.md` | `backend-rbac-rules.md` |
| inspect report-center behavior | `exmodule-report-guide.md` | `backend-dpa-guide.md` |
| inspect template-center behavior | `exmodule-tpl-guide.md` | `email-capability-guide.md` |
| inspect UI config-center behavior | `exmodule-ui-guide.md` | `dual-side-development.md` |
| inspect workflow-engine behavior | `exmodule-workflow-guide.md` | `backend-rbac-rules.md` |
| inspect attachment upload plus storage integration | `attachment-storage-integration-guide.md` | `exmodule-ambient-guide.md` |
| inspect static modeling chain | `static-modeling-guide.md` | `crud-engine-guide.md` |
| inspect ambient activity logging | `activity-log-guide.md` | `exmodule-ambient-guide.md` |
| inspect configurable report center | `report-center-guide.md` | `exmodule-report-guide.md` |
| inspect ACL and reinforced authorization | `acl-authorization-guide.md` | `backend-rbac-rules.md` |
| inspect modular config with unified UI | `modulat-ui-unified-guide.md` | `exmodule-modulat-guide.md` |
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
3. `mcp-integration-map.md`
4. `mcp-fast-retrieval-rules.md`
5. `zero-version-guide.md`
6. `config-center-local-nacos.md`
7. `dbs-multi-datasource.md`
8. `zero-overlay-bridge.md`
9. `job-model-guide.md`
10. `plugin-layer-map.md`
11. `cache-redis-guide.md`
12. `excel-import-export-guide.md`
13. `monitor-center-guide.md`
14. `security-plugin-flow.md`
15. `exmodule-boundary.md`
16. `spi-registry-map.md`
17. `extension-api-guide.md`
18. `backend-dpa-guide.md`
19. `dbe-query-rules.md`
20. `crud-engine-guide.md`
21. `search-hints.md`
22. `graph-usage-rules.md`
23. `mcp-code-review-graph-rules.md`
24. `distillation-rules.md`
25. `purification-rules.md`
26. `dual-side-development.md`
27. `evolution-rules.md`
