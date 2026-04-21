# Project Rule Awareness (Harness 3.0)

This document defines how AI agents should treat project-local rule files (MDC, `.mdc`, `.trae`, and equivalent) when working in a Zero / Vert.x-first framework project.

---

## Core Principle

Project-local rule files are an **optional, project-scoped rule layer**.
They refine agent behavior for a specific project.
They do not override framework fundamentals.
They must never be promoted into framework-level knowledge.

---

## MDC Awareness Rules

### Rule 1 — Inspect if present, ignore if absent

If project-local rule files exist (`.cursor/rules/*.mdc`, `.trae/rules/*.md`, or equivalent), inspect them before starting implementation.

If no rule files exist, ignore this layer entirely and continue with:
- `mxt/` framework docs
- source code
- `.r2mo` specs
- project `CLAUDE.md` / `AGENTS.md`

Do **not** block, loop, or ask the user about missing MDC files.

---

### Rule 2 — `devapi.mdc` is the highest-priority backend rule file

If `devapi.mdc` (or equivalent API execution rule) exists, read it first before:
- implementing backend APIs
- validating request / response contracts
- extracting data structures
- deciding schema / DTO / domain mapping
- checking interface behavior

In Zero projects, this is especially important because:
- The CRUD engine covers many standard interfaces without hand-written code
- `devapi.mdc` defines how to determine whether a given interface belongs to the CRUD engine path or requires custom implementation
- It also defines how to extract data structure requirements from `.r2mo` contracts

Its absence means: continue with `.r2mo`, `mxt/`, and source code directly.

---

### Rule 3 — Extract only what is reusable within that project

From any MDC file, extract only:
- directory or module boundaries
- layering and naming conventions
- allowed and forbidden modification zones
- frontend / backend / integration workflow constraints
- tooling or generation rules specific to this project's stack

Do **not** promote project-specific naming, entity names, module IDs, or business terms into framework-level guidance.

---

### Rule 4 — MDC files are not cross-project portable

Different Zero projects may have different MDC rule sets.
An MDC rule valid in one project may be wrong or irrelevant in another.
Always treat MDC as scoped to the current project, not as universal truth.

---

### Rule 5 — MDC refines, framework guides

Framework `mxt/` docs define structural and semantic rules.
MDC files define project-local execution preferences.
When they conflict, prefer framework rules unless the MDC explicitly overrides a specific behavior for a documented reason.

---

## Agent Execution Flow for Zero Projects (Harness 3.0)

```text
1. Detect stack      → Zero / Vert.x-first (from root pom.xml, zero-0216 parent)
2. Read mxt/         → framework-map, abstraction-rules, spi-registry-map, spi-implementation-rules, backend-dpa-guide, dbe-query-rules
3. Check MDC layer   → if exists: inspect devapi.mdc first, then other backend/frontend/integration mdc files
4. Extract project constraints from MDC (if present)
5. Check CRUD engine → does this requirement fit zero-extension-crud + entity.json?
6. Read .r2mo specs  → operations, schemas, proto, domain contracts
7. Proceed with CRUD engine (zero-code) or hand-written DPA as appropriate
8. If MDC absent at step 3: skip directly to step 5
```

---

## Zero-specific MDC Signal Words

When scanning MDC files in a Zero project, watch for these high-signal keywords:

- `entity.json` — metadata-driven model configuration
- `column.json` — column/view metadata
- `zero-extension-crud` — CRUD engine reference
- `seekSyntax` — row-level security / RBAC filter
- `QQuery` / `QTree` — query protocol
- `Agent / Addr / Actor` — Zero runtime chain
- `sigma` / `tenantId` / `appId` — multi-tenant context
- `SPI` / `Ex*` / `Sc*` — extension points

If these appear in MDC, the file contains Zero-specific guidance that should be applied directly.

---

## Summary

> MDC is a lens, not a replacement.
> Use it to see the project more clearly.
> Don't let it blind you to the framework.
