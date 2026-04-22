# MXT Document Boundary Audit

> Final SRP audit for the current `mxt/*.md` pack.
> The goal is not maximum fragmentation. The goal is one clear owner concern per file.

## 1. Audit Categories

- `Pass` — one clear primary concern
- `Borderline` — still usable, but the scope is wider than ideal
- `Split Recommended` — more than one stable concern is mixed and should be separated

## 2. Audit Result

| Document | Result | Primary Responsibility | Notes |
|---|---|---|---|
| `README.md` | Pass | pack index and reading entry | correct index role |
| `agent-quick-start.md` | Pass | first-pass reading protocol | concise entrypoint |
| `framework-map.md` | Pass | framework layer topology | broad but still one owner concern |
| `zero-epoch-runtime-guide.md` | Pass | dedicated runtime-layer ownership | dedicated core-runtime document |
| `zero-boot-wiring-guide.md` | Pass | dedicated boot-wiring ownership | dedicated zero-boot document |
| `mcp-integration-map.md` | Pass | topic-to-module and topic-to-query routing | new MCP-facing routing layer without taking over graph discipline |
| `mcp-fast-retrieval-rules.md` | Pass | shortest MCP retrieval discipline | dedicated token-saving retrieval document |
| `distillation-rules.md` | Pass | document distillation rules | dedicated compression and anchor-retention document |
| `purification-rules.md` | Pass | duplicate rule purification | dedicated rule ownership cleanup document |
| `zero-version-guide.md` | Pass | BOM and managed dependency ownership | dedicated version-surface document |
| `abstraction-rules.md` | Pass | naming and code abstraction conventions | single concern |
| `spi-core-plugin-guide.md` | Pass | core SPI architecture across plugin and extension layers | dedicated SPI-architecture document |
| `config-center-local-nacos.md` | Pass | local plus Nacos config-center ownership | dedicated config-center document |
| `dbs-multi-datasource.md` | Pass | static and dynamic data-source registration | dedicated DBS document |
| `zero-overlay-bridge.md` | Pass | overlay bridge and shared platform contracts | dedicated overlay document |
| `job-model-guide.md` | Pass | framework job model and mission extraction | dedicated job document |
| `plugin-layer-map.md` | Pass | plugin capability boundary | single concern |
| `cache-redis-guide.md` | Pass | cache family and Redis capability ownership | dedicated cache document |
| `elasticsearch-guide.md` | Pass | Elasticsearch capability ownership | dedicated Elasticsearch document |
| `excel-import-export-guide.md` | Pass | Excel plugin startup and import/export anchors | dedicated Excel document |
| `monitor-center-guide.md` | Pass | monitor plugin family ownership | dedicated monitor document |
| `security-plugin-flow.md` | Pass | security plugin family and provider flow | dedicated security plugin document |
| `session-guide.md` | Pass | session capability ownership | dedicated session document |
| `oauth2-capability-guide.md` | Pass | OAuth2 capability ownership | dedicated OAuth2 capability document |
| `neo4j-guide.md` | Pass | Neo4j capability ownership | dedicated Neo4j document |
| `swagger-openapi-guide.md` | Pass | Swagger/OpenAPI capability ownership | dedicated Swagger document |
| `trash-capability-guide.md` | Pass | trash capability ownership | dedicated trash document |
| `email-capability-guide.md` | Pass | email capability vs email-auth boundary | dedicated email document |
| `sms-capability-guide.md` | Pass | SMS capability vs SMS-auth boundary | dedicated SMS document |
| `weco-capability-guide.md` | Pass | Weco capability vs Weco security boundary | dedicated Weco document |
| `websocket-guide.md` | Pass | WebSocket capability ownership | dedicated WebSocket document |
| `exmodule-boundary.md` | Pass | exmodule vs plugin vs app boundary | single concern |
| `extension-api-guide.md` | Pass | `zero-extension-api` ownership | dedicated extension-api document |
| `exmodule-ambient-guide.md` | Pass | ambient reusable business ownership | dedicated exmodule document |
| `exmodule-erp-guide.md` | Pass | ERP reusable business ownership | dedicated exmodule document |
| `exmodule-finance-guide.md` | Pass | finance reusable business ownership | dedicated exmodule document |
| `exmodule-graphic-guide.md` | Pass | graphic reusable business ownership | dedicated exmodule document |
| `exmodule-integration-guide.md` | Pass | integration reusable business ownership | dedicated exmodule document |
| `exmodule-lbs-guide.md` | Pass | LBS reusable business ownership | dedicated exmodule document |
| `exmodule-mbseapi-guide.md` | Pass | MBSE API reusable business ownership | dedicated exmodule document |
| `exmodule-mbsecore-guide.md` | Pass | MBSE core reusable business ownership | dedicated exmodule document |
| `exmodule-modulat-guide.md` | Pass | modulat reusable business ownership | dedicated exmodule document |
| `exmodule-rbac-guide.md` | Pass | RBAC reusable business ownership | dedicated exmodule document |
| `exmodule-report-guide.md` | Pass | report reusable business ownership | dedicated exmodule document |
| `exmodule-tpl-guide.md` | Pass | TPL reusable business ownership | dedicated exmodule document |
| `exmodule-ui-guide.md` | Pass | UI config-center reusable ownership | dedicated exmodule document |
| `exmodule-workflow-guide.md` | Pass | workflow reusable business ownership | dedicated exmodule document |
| `attachment-storage-integration-guide.md` | Pass | attachment plus storage integration ownership | dedicated composite-topic document |
| `static-modeling-guide.md` | Pass | static modeling chain ownership | dedicated composite-topic document |
| `activity-log-guide.md` | Pass | activity logging ownership | dedicated composite-topic document |
| `report-center-guide.md` | Pass | configurable report-center ownership | dedicated composite-topic document |
| `acl-authorization-guide.md` | Pass | ACL and reinforced authorization ownership | dedicated composite-topic document |
| `modulat-ui-unified-guide.md` | Pass | modulat plus UI combined ownership | dedicated composite-topic document |
| `backend-dpa-guide.md` | Pass | backend DPA structure and async contract | split from the old backend guide |
| `dbe-query-rules.md` | Pass | DBE and query semantics | split from the old backend guide |
| `backend-model-tenant-rules.md` | Pass | model metadata and tenant rules | split from the old backend guide |
| `backend-rbac-rules.md` | Pass | backend RBAC resource rules | split from the old backend guide |
| `crud-engine-guide.md` | Pass | CRUD-engine-first decision model | split from the old backend guide |
| `contract-source-rules.md` | Pass | `.r2mo` contract authority | split from the old integration guide |
| `backend-api-integration.md` | Pass | backend integration from contract to runtime | split from the old integration guide |
| `frontend-client-integration.md` | Pass | frontend client integration rules | split from the old integration guide |
| `environment-contracts.md` | Pass | environment-level runtime and integration contracts | split from the old integration guide |
| `extension-points.md` | Pass | legacy SPI navigation page | retained only as an index page |
| `extension-skeleton-guide.md` | Pass | `zero-extension-skeleton` ownership | dedicated extension-skeleton document |
| `spi-registry-map.md` | Pass | SPI family map | split from the old extension document |
| `spi-implementation-rules.md` | Pass | SPI implementation and stability rules | split from the old extension document |
| `dual-side-development.md` | Pass | backend/frontend collaboration boundary | single concern |
| `project-rule-awareness.md` | Pass | project-local rules vs framework rules | single concern |
| `evolution-rules.md` | Pass | maintenance and sync policy for the pack | single concern |
| `search-hints.md` | Borderline | text-search navigation hints | now also includes some graph-operational advice; acceptable but near the boundary |
| `mcp-code-review-graph-rules.md` | Pass | graph-assisted playbook selection | reduced to playbooks only |
| `backend-dev-guide.md` | Pass | legacy backend navigation page | retained only as an index page |
| `integration-guide.md` | Pass | legacy integration navigation page | retained only as an index page |
| `graph-usage-rules.md` | Pass | operational graph usage rules | new dedicated file |
| `flyway-loading-flow.md` | Pass | Flyway loading and migration extension flow | new dedicated file |
| `actor-startup-matrix.md` | Pass | `@Actor` startup matrix semantics | new dedicated file |
| `buildapp-buildperm-flow.md` | Pass | installation-time app/RBAC bootstrap flow | new dedicated file |
| `oauth2-init-flow.md` | Pass | OAuth2 initialization and Flyway dependency | new dedicated file |

## 3. Conclusions

The current pack is acceptable as the final structure.

One file remains broader than average, but it is intentionally retained as a high-value entry document instead of being split further:

1. `search-hints.md`

## 4. Recommended Next Split Order

No further split is required now.

If a future framework upgrade makes them materially larger, use this order:

1. split graph-operational content further if `search-hints.md` grows again
2. keep `mcp-integration-map.md` focused on topic routing, not graph commands
3. keep `mcp-code-review-graph-rules.md` focused on playbooks, not topology or entry rules

## 5. Governance Rule

Use this rule for future MXT additions:

```text
One file should answer one class of question for an agent.
If a reader would naturally ask for a different search anchor, owner layer, or runtime phase, it likely needs a separate file.
```
