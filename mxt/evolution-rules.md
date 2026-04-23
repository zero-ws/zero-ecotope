# Evolution Rules

> This document defines how the Zero Ecotope MXT knowledge pack must evolve after framework upgrades, module splits, or boundary changes.

## 1. Purpose

`mxt/` is not static prose. It is a stable navigation layer for humans and AI agents.
Whenever code structure, module boundaries, or extension patterns change, the knowledge pack must be updated so agents can still answer:
- Which layer does this code belong to?
- Where should new code be placed?
- Which files should be searched first?

## 2. Mandatory Sync Cases

### 2.1 Core boundary changes

If any of the following changes:
- `zero-epoch` sub-modules are added, removed, renamed, or split
- core abstractions such as `HPI`, `DBE`, `Fx/Ux/Ut`, `HED`, or `HOI` change responsibility
- lifecycle markers such as `#BOOT-*`, `#REQ-*`, `#SPI`, or `#PIN` change
- the relationship between `zero-boot` and `zero-epoch` changes

Then update at least:
- `zero-boot-wiring-guide.md`
- `framework-map.md`
- `abstraction-rules.md`
- `search-hints.md`
- `README.md` if the entry narrative changes

### 2.2 Plugin boundary changes

If any of the following changes:
- a `zero-plugins-equip` module is added, removed, renamed, merged, or split
- plugin grouping changes significantly
- a capability moves from plugin layer to exmodule layer or the reverse
- plugin consumption no longer happens through the same framework abstractions

Then update at least:
- `plugin-layer-map.md`
- `exmodule-boundary.md`
- `search-hints.md`
- `framework-map.md` if layer shape changed
- `README.md` if the entry explanation changed

### 2.3 Extension / exmodule boundary changes

If any of the following changes:
- `zero-extension-skeleton` adds or removes SPIs
- `ExBoot.SPI_SET` changes
- `zero-extension-crud` or `zero-extension-api` changes role
- any `zero-exmodule-*` is added, merged, split, deprecated, or renamed
- the `api/domain/provider` structure changes

Then update at least:
- `extension-skeleton-guide.md`
- `spi-registry-map.md`
- `spi-implementation-rules.md`
- `exmodule-boundary.md`
- `framework-map.md`
- `search-hints.md`
- `README.md` if document entry or reading order changes

## 3. When to Create a New Markdown File

Create a new MXT document when one of these is true:

1. A new stable layer appears.
2. A new long-lived rule set appears.
3. An existing document becomes overloaded with multiple responsibilities.
4. The new topic will be referenced repeatedly after future upgrades.

Examples:
- a dedicated cloud/gateway/AI-orchestration layer
- a reusable security-resource model guide
- a migration-specific playbook that no longer fits inside `evolution-rules.md`

If a new markdown file is created:
- add it to `README.md`
- add search guidance for it in `search-hints.md`
- decide whether it owns topic routing, graph discipline, or source semantics, and keep only one of those
- run the new file through `distillation-rules.md` and `purification-rules.md`

## 4. When to Update an Existing File Instead

Prefer updating existing files when the change is only:
- a module count change without a new layer
- a renamed SPI without a new extension model
- a plugin list refresh without a boundary change
- a scenario wording change without structural impact

Rule of thumb:
- naming/count/responsibility tweaks → update existing files
- layer/boundary/model changes → add a new file or split an old one

## 5. Entry Document Rewrite Rules

### README.md

Always keep it valid as the entry document:
- list the full current document set
- preserve the AI-first framing
- reflect the current Zero core / plugin / extension / exmodule understanding
- keep the reading order useful for new agents

### framework-map.md

Always keep it valid as the structural map:
- root and ecosystem hierarchy must match current POM structure
- layer order must match current module reality
- scenario mapping must match current framework intent

### search-hints.md

Always keep it valid as the shortest-path navigation doc:
- search patterns must still hit real code
- key SPI discovery locations must still be correct
- layer placement hints must still reflect the real codebase

If search keywords stop working, this file must be updated immediately.

### MCP fast retrieval rules

Always keep `mcp-fast-retrieval-rules.md` valid for token-saving AI Agent consumption:

- route once from topic to owner
- open only the owner document and proof anchor by default
- escalate to graph playbooks only when owner evidence is insufficient

### Coverage audit script

Run this after module or MXT document changes:

```bash
python3 .r2mo/task/audit_mxt_coverage.py
```

The script checks owner documents, MCP route coverage, internal Markdown references, English-only MXT content, and graph-noise governance anchors.

### Audit trigger rule

Any commit that modifies files under `mxt/` or `zero-ecosystem/pom.xml` should trigger the coverage audit. Implementation options:

- **Git pre-commit hook**: add `audit_mxt_coverage.py` to `.git/hooks/pre-commit` with path filtering on `mxt/` and `zero-ecosystem/pom.xml`.
- **CI step**: add the audit as a CI job triggered on `mxt/**` and `zero-ecosystem/pom.xml` path changes.
- **Manual fallback**: if neither hook nor CI is configured, the agent making the change must run the audit and report the result in the commit message.

Minimum requirement: the audit must pass before any MXT or module-structure change is merged.

## 6. Agent Verification After Upgrade

After a framework upgrade, the minimum agent verification set is:

### 6.1 Directory verification
- confirm the required `mxt/` files exist
- confirm `README.md` links to all current files

### 6.2 POM verification
- confirm root `pom.xml` still matches `framework-map.md`
- confirm `zero-ecosystem/pom.xml` still matches the layer map
- confirm `zero-version-extension/pom.xml` still matches the exmodule list

### 6.3 SPI verification
- confirm `ExBoot.SPI_SET` still matches `spi-registry-map.md`
- confirm `HPI.findMany`, `interface Ex`, `interface Sc`, and `interface Ui` remain useful search anchors

### 6.4 Boundary verification
- confirm plugin layer still contains capability rather than domain rules
- confirm exmodules still hold reusable business meaning
- confirm application layer still remains the final customization layer

### 6.5 Search-hit verification
Agents should verify that the following still locate the correct areas:
- `<module>zero-plugins-extension</module>`
- `SPI_SET`
- `HPI.findMany`
- `zero-exmodule-`
- `UiForm`
- `class Addr`

If any of these no longer hit correctly, update `search-hints.md` first.

## 7. Recommended Upgrade Order

After each framework upgrade:

1. verify real module structure from POMs and directories
2. update `framework-map.md`
3. update `plugin-layer-map.md`, `spi-registry-map.md`, `spi-implementation-rules.md`, and `exmodule-boundary.md`
4. update `abstraction-rules.md` and `search-hints.md`
5. finally update `README.md`

## 8. Final Principle

- `README.md` owns entry and reading order
- `framework-map.md` owns structure
- `zero-boot-wiring-guide.md` owns `zero-boot` launch and boot wiring
- `mcp-integration-map.md` owns topic routing from framework concern to owner modules and graph targets
- `mcp-fast-retrieval-rules.md` owns shortest-path MCP retrieval and token-saving stop rules
- `distillation-rules.md` owns document compression and evidence retention rules
- `purification-rules.md` owns duplicate-rule cleanup and owner precedence
- `graph-noise-rules.md` owns graph-noise filtering and known noisy path rules
- `plugin-layer-map.md` owns capability plugins
- `spi-registry-map.md` owns SPI family mapping
- `spi-implementation-rules.md` owns SPI implementation rules
- `extension-skeleton-guide.md` owns `zero-extension-skeleton`
- `exmodule-boundary.md` owns layer boundaries
- `exmodule-rbac-guide.md` owns `zero-exmodule-rbac`
- `search-hints.md` owns navigation
- `graph-usage-rules.md` owns graph discipline
- `mcp-code-review-graph-rules.md` owns graph playbooks
- `ai-decision-tree.md` owns top-level AI agent decision logic and one-question-one-answer routing
- `ai-anti-patterns.md` owns common AI agent mistakes and their correct paths
- `evolution-rules.md` owns long-term maintenance of the pack

The pack is healthy only if a new AI agent can still answer, quickly and correctly, where code belongs and where to search first.
