# Zero Framework Graph Navigation Rules (Final)

> Final framework-grade rules for AI agents that need to read, explain, debug, extend, or map requirements onto the Zero framework.
> This document is written for **all downstream projects** that depend on Zero, not for one specific application.

---

## 1. Purpose

This file defines the **framework-level reading protocol** for AI agents consuming `mxt-zero`.

Its purpose is to help agents:

- understand how a downstream project requirement maps onto Zero framework code
- find the correct owning layer inside `zero-ecotope`
- use `code-review-graph` safely and effectively
- avoid confusing framework semantics with project-local semantics
- avoid over-trusting graph results for metadata/resource-heavy behavior
- produce repeatable, evidence-based conclusions that other projects can reuse

This document is **not** a replacement for the rest of `mxt/`.
It is the graph-assisted routing layer that connects:

```text
project requirement -> framework docs -> graph search -> source verification -> ownership decision
```

---

## 2. Who Should Use This File

Load this file when an AI agent is working on any project that is Zero / Vert.x-first and one or more of the following is true:

- the active project inherits or imports `io.zerows:zero-0216`
- the user asks about `mxt-zero`, `zero-ecotope`, `zero-epoch`, `zero-boot`, `zero-plugins-equip`, `zero-plugins-extension`, `zero-extension-*`, or `zero-exmodule-*`
- the task needs framework-level reading rather than project-only reading
- the task requires graph-assisted structural analysis, impact analysis, ownership analysis, or extension-point analysis
- the requirement mentions CRUD, DBE, QQuery, SPI, RBAC, plugin capability, EventBus flow, or frontend extension alignment

Do **not** load this file for non-Zero stacks.
Do **not** treat R2MO-first and Zero-first as interchangeable.

---

## 3. Framework Consumption Model for Downstream Projects

A downstream project that depends on Zero should be read in this order:

```text
Project-local rules/specs
    -> Zero mxt docs
    -> code-review-graph structural queries
    -> framework source verification
    -> ownership classification
```

Interpretation:

1. **Project-local context comes first**
   - project `CLAUDE.md` / `AGENTS.md`
   - `.cursor/rules/*.mdc` or equivalent
   - `.r2mo` or local API/model specs
2. **Framework context comes second**
   - `mxt/README.md`
   - `mxt/framework-map.md`
   - other `mxt/*.md`
3. **Graph search comes third**
   - to find relationships, paths, call patterns, and candidate owners
4. **Source verification comes fourth**
   - graph narrows candidates; source proves behavior
5. **Ownership decision comes last**
   - project, exmodule, extension skeleton, plugin, boot, or core runtime

One-line rule:

```text
A downstream project's requirement is local; the explanation of reusable behavior is framework-owned.
```

---

## 4. Project vs Framework Boundary

This file is framework-scoped.

Agents must keep this boundary strict:

### Project-local truth

These may refine how a specific downstream project uses Zero:

- project MDC / rule files
- local naming conventions
- local module IDs
- app-specific entities, APIs, workflows, roles
- local `.r2mo` requirements

### Framework-level truth

These belong in `mxt-zero` and are reusable across downstream projects:

- layer ownership (`zero-epoch`, `zero-boot`, `zero-plugins-equip`, `zero-plugins-extension`)
- SPI contracts and loading patterns
- CRUD engine routing rules
- DBE / QQuery / runtime query semantics
- plugin vs exmodule vs app placement rules
- runtime/env contract rules

Do **not** promote project-specific naming or business semantics into framework guidance.
Do **not** use one downstream project's local rule file as global Zero truth.

---

## 5. Canonical Knowledge Sources

When reading Zero, use these sources in order.

### 5.1 Framework docs

Start with:

- `mxt/README.md`
- `mxt/framework-map.md`

Then choose the relevant specialist document:

| Need | Document |
|---|---|
| layer ownership | `framework-map.md` |
| high-order objects / markers | `abstraction-rules.md` |
| plugin capability boundary | `plugin-layer-map.md` |
| SPI family map | `spi-registry-map.md` |
| SPI implementation and registration rules | `spi-implementation-rules.md` |
| exmodule vs plugin vs app boundary | `exmodule-boundary.md` |
| backend DPA / async contract | `backend-dpa-guide.md` |
| DBE / query syntax | `dbe-query-rules.md` |
| metadata / tenant model rules | `backend-model-tenant-rules.md` |
| backend RBAC rules | `backend-rbac-rules.md` |
| standard CRUD decision model | `crud-engine-guide.md` |
| project-local rule interaction | `project-rule-awareness.md` |
| fast source anchors | `search-hints.md` |
| graph-assisted reading | `mcp-code-review-graph-rules.md` |

### 5.2 Framework source

Primary framework source roots:

```text
zero-ecotope/zero-ecosystem/zero-epoch
zero-ecotope/zero-ecosystem/zero-boot
zero-ecotope/zero-ecosystem/zero-plugins-equip
zero-ecotope/zero-ecosystem/zero-plugins-extension
zero-ecotope/zero-ui
```

### 5.3 Downstream project context

When working from a downstream project, also inspect:

- project `pom.xml`
- project local rules
- `.r2mo` contracts or app specs
- app resources under `plugins/` and `src/main/resources/`

---

## 6. Code Review Graph Connection

`code-review-graph` is the structural navigation companion for this framework.

Current repository identity:

```text
Alias: mxt-zero
Repo:  /Users/lang/zero-cloud/app-zero/zero-ecotope
Graph: /Users/lang/zero-cloud/app-zero/zero-ecotope/.code-review-graph/graph.db
```

Useful CLI commands:

```bash
code-review-graph repos
code-review-graph status --repo /Users/lang/zero-cloud/app-zero/zero-ecotope
code-review-graph build --repo /Users/lang/zero-cloud/app-zero/zero-ecotope
code-review-graph update --repo /Users/lang/zero-cloud/app-zero/zero-ecotope
code-review-graph detect-changes --repo /Users/lang/zero-cloud/app-zero/zero-ecotope --base HEAD~1 --brief
```

Useful MCP tool classes:

| Need | Tool |
|---|---|
| fast orientation | `get_minimal_context` |
| graph stats | `list_graph_stats` |
| symbol/file search | `semantic_search_nodes` |
| callers/callees/imports/children | `query_graph` |
| blast radius | `get_impact_radius`, `detect_changes` |
| execution paths | `list_flows`, `get_flow`, `get_affected_flows` |
| architecture clustering | `list_communities`, `get_community`, `get_architecture_overview` |
| multi-repo search | `cross_repo_search` |

Mandatory rule:

```text
Always pass the explicit repo_root for zero-ecotope when calling graph tools.
```

Reason:
A downstream agent may be operating from an application repository; without explicit `repo_root`, graph queries may run against the wrong repository.

---

## 7. Graph Baseline and Interpretation

Last verified graph baseline for this repository:

```text
command: code-review-graph build --repo /Users/lang/zero-cloud/app-zero/zero-ecotope
branch:  master
commit:  28f4a7a4c0788e02ecfd1476c7b866db3e60229f
files:   3932
nodes:   63217
edges:   351090
languages: bash, javascript, java
postprocess: full
communities: 68
flows: 10458
risk_index rows: 59285
```

Interpretation rules:

- the graph contains framework Java, `zero-ui` JavaScript, shell scripts, and some repository-local JS/doc tooling
- the graph is useful for structure and relationships, not for every resource/config artifact
- the graph may surface communities whose names are not meaningful to Zero architecture if path filtering is not applied

Noise-control rules:

- prefer `zero-ecosystem/` for backend framework analysis
- prefer `zero-ui/src/extension/` for frontend-extension analysis
- ignore `.obsidian/plugins` and similar repository tooling paths unless the task explicitly targets them
- do not treat community names alone as architecture truth; always validate the member paths

---

## 8. Reading Order for AI Agents

Use this fixed protocol.

### Step 1 — detect the active context

From the downstream project, confirm:

- stack is Zero-first
- requirement is framework-related rather than purely app-local
- whether the task is code-driven, resource-driven, or mixed

### Step 2 — load framework ownership

Read in this minimum order:

1. `mxt/README.md`
2. `mxt/framework-map.md`
3. one or more of:
   - `plugin-layer-map.md`
   - `spi-registry-map.md`
   - `spi-implementation-rules.md`
   - `exmodule-boundary.md`
   - `backend-dpa-guide.md`
   - `dbe-query-rules.md`
   - `search-hints.md`
   - `project-rule-awareness.md`

### Step 3 — classify the owning layer

Classify into one of:

- core runtime (`zero-epoch`)
- boot wiring (`zero-boot`)
- infrastructure plugin (`zero-plugins-equip`)
- extension contract (`zero-extension-skeleton` / `zero-extension-api` / `zero-extension-crud`)
- reusable business module (`zero-exmodule-*`)
- downstream project application layer

### Step 4 — use graph search only after the layer is known

Search for:

- the exact symbol
- the exact module
- the exact owning path prefix

### Step 5 — open the returned source files

Verify:

- signatures
- interfaces
- event addresses
- SPI membership
- service vs transport boundary
- actual file ownership

### Step 6 — only then explain or change behavior

The correct final output must answer:

- what owns this behavior
- why that layer owns it
- what should change
- what should not change

---

## 9. Layer Map for Downstream Agents

Use these path filters first.

| Concern | First path filter | Meaning |
|---|---|---|
| core runtime | `zero-ecosystem/zero-epoch/` | runtime execution, DBE, request handling, configuration internals |
| boot | `zero-ecosystem/zero-boot/` | startup wiring, launcher, extension boot hookup |
| plugin capability | `zero-ecosystem/zero-plugins-equip/zero-plugins-*` | infrastructure adapters and reusable capability |
| extension skeleton | `zero-ecosystem/zero-plugins-extension/zero-extension-skeleton/` | SPI contracts, boot registration, reusable extension contracts |
| CRUD engine | `zero-ecosystem/zero-plugins-extension/zero-extension-crud/` | metadata-driven CRUD runtime |
| extension API support | `zero-ecosystem/zero-plugins-extension/zero-extension-api/` | reusable API-side extension conventions |
| reusable business module | `zero-ecosystem/zero-plugins-extension/zero-exmodule-*` | reusable domain behavior |
| frontend extension | `zero-ui/src/extension/` | frontend components aligned with backend extension contracts |

Key source anchors by layer:

| Layer | High-value anchors |
|---|---|
| runtime | `HPI`, `Ux`, `Ut`, `DBE`, `ConfigMod`, `#BOOT-`, `#REQ-` |
| boot | launcher classes, extension boot assembly, module loading |
| plugin | provider/client abstractions, third-party adapters |
| extension skeleton | `ExBoot`, `SPI_SET`, `spi/`, `META-INF/services`, `HPI.findMany` |
| CRUD | `Addr`, search/import/export paths, metadata-owned runtime behavior |
| exmodule | `*-api`, `*-domain`, `*-provider`, `serviceimpl`, `spi`, resources |
| frontend extension | `UiForm`, `UiValve`, `UiApeak`, extension component trees |

---

## 10. Framework Triage Rules

When a downstream project requirement points into Zero, classify it before reading too much code.

### 10.1 Runtime / request / EventBus problem

Typical signals:

```text
Agent, Actor, Addr, route, endpoint, request flow, EventBus, queue, transport
```

Likely owning layer:

- app-local API module if behavior is app-specific
- reusable exmodule API/provider if behavior is domain-reusable
- Zero runtime if transport/request semantics are framework-wide

### 10.2 CRUD / metadata-driven problem

Typical signals:

```text
entity.json, column.json, import, export, search, batch, metadata-driven, standard list/detail/edit
```

Likely owning layer:

- `zero-extension-crud` first
- exmodule/provider resources second
- handwritten DPA only after confirming CRUD coverage is insufficient

### 10.3 DBE / persistence / query problem

Typical signals:

```text
DBE, QQuery, QTree, criteria, pager, sorter, projection, DAO, jOOQ, dslContext, serviceimpl
```

Likely owning layer:

- runtime store/query semantics: `zero-epoch-store`
- reusable business persistence: exmodule provider
- app-specific persistence: downstream provider layer

### 10.4 SPI / extension-point problem

Typical signals:

```text
SPI, Ex*, Sc*, Ui*, ExBoot, HPI.findMany, META-INF/services, ConfigMod
```

Likely owning layer:

- contract shape: `zero-extension-skeleton`
- implementation: exmodule provider or plugin/app provider
- registration problem: boot/skeleton/resource path

### 10.5 Plugin capability problem

Typical signals:

```text
cache, redis, websocket, elasticsearch, flyway, monitor, oauth2, ldap, jwt, email, sms, session
```

Likely owning layer:

- infrastructure adapter -> plugin layer
- business usage policy -> exmodule/app layer

### 10.6 RBAC / permission resource problem

Typical signals:

```text
RBAC_RESOURCE, RBAC_ROLE, PERM.yml, seekSyntax, seekConfig, dmConfig, action seek, permission model
```

Likely owning layer:

- reusable permission semantics -> `zero-exmodule-rbac`
- project permission data -> downstream resource trees
- transport or runtime security protocol -> plugin/security layer

### 10.7 Runtime env / configuration problem

Typical signals:

```text
vertx.yml, env vars, tenant, appId, namespace, style, language, startup config
```

Likely owning layer:

- runtime/env contract -> configuration layer / `ConfigMod` / `zero-epoch-setting`
- not plugin or exmodule by default

Mandatory rule:

```text
Never rewrite business or plugin logic to compensate for missing runtime/env configuration.
```

---

## 11. Graph Query Playbooks

Use these playbooks when an MCP graph server is available.

### 11.1 Find the owning layer for a symbol

1. `semantic_search_nodes(query='<symbol>', repo_root='<zero-ecotope>')`
2. inspect the returned `file_path`
3. classify the path prefix into runtime / boot / plugin / extension / exmodule / UI
4. open the file directly and verify ownership

### 11.2 Trace request flow into service flow

Use when the task mentions an API, actor, or address.

1. `semantic_search_nodes('Addr', ...)` or `semantic_search_nodes('<ActorName>', ...)`
2. `query_graph(pattern='children_of', target='<Addr target>', ...)`
3. `query_graph(pattern='callers_of', target='<service method>', ...)`
4. `query_graph(pattern='callees_of', target='<actor or service method>', ...)`
5. verify manually against source

Target mental model:

```text
Agent -> Addr -> Actor -> Stub -> Service -> DBE
```

### 11.3 Trace SPI contract to implementation

1. `semantic_search_nodes('ExBoot', ...)`
2. `semantic_search_nodes('<SPI name>', ...)`
3. open `ExBoot.java` and verify presence in `SPI_SET`
4. search `META-INF/services/<fqcn>` directly in filesystem
5. inspect provider implementations in `*-provider`

### 11.4 Analyze framework blast radius

1. `detect_changes` or CLI `code-review-graph detect-changes`
2. `get_impact_radius` for explicit changed files
3. `get_affected_flows` / `list_flows` for critical-path review
4. open source and confirm layer ownership before changing code

### 11.5 Review architecture clusters safely

1. `list_communities(min_size=...)`
2. filter by path prefixes
3. ignore tooling/doc noise
4. use `get_architecture_overview` only after path-based validation

### 11.6 Use graph search in downstream project conversations

When the user asks from a downstream project:

1. extract the business/problem vocabulary from the downstream requirement
2. convert it into framework terms (`CRUD`, `QQuery`, `ScPermit`, `UiForm`, `ConfigMod`, etc.)
3. search the Zero graph using framework terms, not just app terms
4. map the framework result back to the downstream project requirement

This is the key to making a framework knowledge base reusable across many apps.

---

## 12. Graph Blind Spots and Non-Graph Sources

`code-review-graph` is strongest on structural source languages such as Java / JavaScript / Bash.
It is **not** the source of truth for every Zero artifact.

High-risk blind spots:

- `PERM.yml`
- `RBAC_RESOURCE` / `RBAC_ROLE`
- `entity.json` / `column.json`
- `seekSyntax`, `seekConfig`, `dmConfig`
- `vertx.yml`
- Flyway SQL / DDL / seed scripts
- resource trees under `plugins/`
- model metadata trees under `src/main/resources/`
- downstream `.r2mo` or non-parsed contract files

Interpretation rule:

```text
The graph is strong on code structure; resource trees still decide metadata semantics.
```

Mandatory fallback order for resource-heavy tasks:

1. graph search to find owning Java module or SPI
2. direct filesystem search under `plugins/`, `model/`, `security/`, `workflow/`, `src/main/resources/`
3. open the resource/config files directly
4. then return to Java callers/callees only if behavior still needs proof

Do not stop at graph evidence for:

- RBAC/resource diagnosis
- CRUD metadata diagnosis
- runtime configuration diagnosis
- boot/resource loading diagnosis

---

## 13. Framework-Level Architectural Facts Verified in Zero

These facts are reusable across downstream projects.

### 13.1 Top-level Maven structure

Root `pom.xml` declares:

```text
zero-version
zero-0216
zero-ecosystem
```

### 13.2 Runtime ecosystem structure

`zero-ecosystem/pom.xml` expands into:

```text
zero-epoch             # core runtime
zero-boot              # boot wiring
zero-plugins-equip     # infrastructure capability plugins
zero-plugins-extension # extension contracts + CRUD + exmodules
```

### 13.3 Ownership order

Use this ownership order when classifying changes:

```text
Application / Downstream Project
        ↓
zero-plugins-extension
        ↓
zero-plugins-equip
        ↓
zero-boot
        ↓
zero-epoch
```

Interpretation:

- app/project owns local business customization
- exmodule owns reusable domain behavior
- plugin owns reusable capability
- boot owns assembly
- runtime owns execution semantics

### 13.4 Core source anchors

| Anchor | Meaning |
|---|---|
| `HPI` | high-order SPI lookup and override entry |
| `Ux` | business-facing async utility / facade |
| `ConfigMod` | module-level configuration loading contract |
| `ExBoot` | extension SPI registration root |
| CRUD `Addr` | built-in CRUD EventBus surface |
| exmodule `Addr` files | reusable domain EventBus surfaces |

### 13.5 Exmodule reusable shape

Common reusable module pattern:

```text
zero-exmodule-{name}/
├── zero-exmodule-{name}-api
├── zero-exmodule-{name}-domain
├── zero-exmodule-{name}-provider
└── zero-exmodule-{name}-ui   # when present
```

This pattern is framework-relevant because downstream projects often mirror it or consume it.

### 13.6 SPI loading is centralized

`ExBoot.SPI_SET` is the central registration point for framework integration SPIs, business extension SPIs, security SPIs, and UI SPIs.

Framework rule:

```text
If the requirement says “extend behavior,” check existing SPI contracts and Java SPI registrations before adding new direct wiring.
```

---

## 14. Safe Change Rules for Framework Reading

Before editing or recommending edits, classify the issue:

1. downstream project usage issue
2. missing framework abstraction
3. framework bug/default problem
4. shared contract/spec issue
5. mixed case: framework change + downstream adaptation

Apply these rules:

- change the downstream project only when behavior is truly app-specific
- change `zero-exmodule-*` when the behavior is reusable across projects within a domain
- change `zero-extension-skeleton` only when a reusable extension contract must change
- change `zero-plugins-equip` only for infrastructure capability/adapters
- change `zero-epoch` / `zero-boot` only for runtime or boot semantics

Anti-pattern:

```text
Do not keep creating project-local bypasses when the missing capability is obviously framework-owned.
```

---

## 15. Anti-Patterns for Downstream AI Agents

Avoid these mistakes:

- treating one downstream project's rules as global Zero truth
- searching only with app-local business terms and never translating them into framework terms
- trusting graph community names without path validation
- using graph evidence alone for YAML / JSON / metadata-driven behavior
- assuming Zero is EventBus-only and ignoring DBE/jOOQ/DAO paths
- editing plugin code because a requirement mentions cache/email/SMS but the real problem is business semantics
- mutating a shared SPI contract because one exmodule or one app wants a special field
- solving runtime/env misconfiguration by hardcoding business logic
- bypassing CRUD engine evaluation for standard data-management requirements

---

## 16. Maintenance and Refresh Rules

Refresh the graph when any of the following is true:

- branch changed
- commit changed materially for the touched module
- module topology changed in `pom.xml`
- framework Java/JavaScript code changed in `zero-ecosystem/` or `zero-ui/`
- the agent is about to trust impact analysis on stale metadata

Command rules:

```bash
# full rebuild after broad changes or topology changes
code-review-graph build --repo /Users/lang/zero-cloud/app-zero/zero-ecotope

# incremental update for same-branch local edits
code-review-graph update --repo /Users/lang/zero-cloud/app-zero/zero-ecotope

# quick verification
code-review-graph status --repo /Users/lang/zero-cloud/app-zero/zero-ecotope
```

Decision rule:

- use `build` after merges, topology changes, or wide framework edits
- use `update` for ordinary same-branch edits
- rebuild before trusting `detect_changes` if the graph branch/commit context is stale

---

## 17. Final Review Questions for Zero Framework Reading

Before returning a final answer, an AI agent should answer these explicitly:

1. Is the active project really Zero-first?
2. Which layer owns the behavior: runtime, boot, plugin, extension contract, exmodule, or app?
3. Is the task code-driven, resource-driven, or both?
4. Does CRUD metadata already express the requirement?
5. Does an existing SPI already cover the extension need?
6. Is there a runtime/env/configuration cause that should be fixed before changing code?
7. Is there a resource-tree owner that must change together with Java code?
8. Did the graph result come from actual framework paths rather than repository tooling noise?
9. Is the final recommendation reusable across downstream projects, or only valid for one app?

---

## 18. Recommended Evidence Template

When answering a user with graph-assisted framework findings, use this structure:

```text
Context:
- active stack
- downstream project scope
- task type (code/resource/mixed)

Layer:
- <runtime|boot|plugin|extension|exmodule|app>

Graph evidence:
- <tool/query + target>
- <tool/query + target>

Source evidence:
- <path>
- <path>

Resource evidence:
- <path>          # if applicable

Conclusion:
- what owns the behavior
- what should change
- what should not change
- whether the conclusion is framework-reusable or app-local
```

This template forces framework answers to stay ownership-first, evidence-based, and reusable across projects.

---

## 19. Summary Rule

> Downstream projects supply the local requirement.
> `mxt-zero` supplies the reusable framework reading model.
> `code-review-graph` supplies structural acceleration.
> Source files and resource trees still supply the final proof.

---

For graph operations and startup-adjacent topics, continue with:

- `graph-usage-rules.md`
- `flyway-loading-flow.md`
- `actor-startup-matrix.md`
- `buildapp-buildperm-flow.md`
- `oauth2-init-flow.md`
