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
| `abstraction-rules.md` | Pass | naming and code abstraction conventions | single concern |
| `plugin-layer-map.md` | Pass | plugin capability boundary | single concern |
| `exmodule-boundary.md` | Pass | exmodule vs plugin vs app boundary | single concern |
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
| `spi-registry-map.md` | Pass | SPI family map | split from the old extension document |
| `spi-implementation-rules.md` | Pass | SPI implementation and stability rules | split from the old extension document |
| `dual-side-development.md` | Pass | backend/frontend collaboration boundary | single concern |
| `project-rule-awareness.md` | Pass | project-local rules vs framework rules | single concern |
| `evolution-rules.md` | Pass | maintenance and sync policy for the pack | single concern |
| `search-hints.md` | Borderline | text-search navigation hints | now also includes some graph-operational advice; acceptable but near the boundary |
| `mcp-code-review-graph-rules.md` | Borderline | graph navigation protocol | large, but now materially cleaner after startup-topic extraction |
| `backend-dev-guide.md` | Pass | legacy backend navigation page | retained only as an index page |
| `integration-guide.md` | Pass | legacy integration navigation page | retained only as an index page |
| `graph-usage-rules.md` | Pass | operational graph usage rules | new dedicated file |
| `flyway-loading-flow.md` | Pass | Flyway loading and migration extension flow | new dedicated file |
| `actor-startup-matrix.md` | Pass | `@Actor` startup matrix semantics | new dedicated file |
| `buildapp-buildperm-flow.md` | Pass | installation-time app/RBAC bootstrap flow | new dedicated file |
| `oauth2-init-flow.md` | Pass | OAuth2 initialization and Flyway dependency | new dedicated file |

## 3. Conclusions

The current pack is acceptable as the final structure.

Two files remain broader than average, but they are intentionally retained as high-value entry documents instead of being split further:

1. `search-hints.md`
2. `mcp-code-review-graph-rules.md`

## 4. Recommended Next Split Order

No further split is required now.

If a future framework upgrade makes them materially larger, use this order:

1. split graph-operational content further if `search-hints.md` grows again
2. keep `mcp-code-review-graph-rules.md` focused on graph-guided reading, not startup subtopics

## 5. Governance Rule

Use this rule for future MXT additions:

```text
One file should answer one class of question for an agent.
If a reader would naturally ask for a different search anchor, owner layer, or runtime phase, it likely needs a separate file.
```
