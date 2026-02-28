---
title: "02-02. Zero后端开发( Vert.x )"
filename: "CLAUDE-BACK-ZERO.md"
---

# CLAUDE-BACK-ZERO — Backend Rules (Vert.x / Zero + DBE)

> Summary of **r2-backend-zero** and **r2-backend-dbe** (DBE is shared across backend docs). Both have **`alwaysApply: true`**. Canonical definitions: `.cursor/rules/*.mdc`. This file embeds **two** mdc rules: DBE (shared) and Zero/Vert.x DPA.

---

## Core rule: Missing .mdc files

**When using this document or following the index:** Referenced `.mdc` files **may not exist**. When processing:

- **If a referenced .mdc file does not exist:** Treat that rule as absent; ignore it and continue. Do not error, block, or assume the file will appear later.
- **Rely on this .md summary** when the corresponding .mdc is missing.

---

## 1. Index (Rule Paths After Install)

This file consolidates **two** rules (each doc has both):

| Rule file | Purpose | Full path |
| :--- | :--- | :--- |
| r2-backend-dbe | DBE query syntax (QQuery / QTree) — **shared** | [.cursor/rules/r2-backend-dbe.mdc](.cursor/rules/r2-backend-dbe.mdc) |
| r2-backend-zero | Zero framework DPA (Agent/Actor/Stub/Service/DBE) | [.cursor/rules/r2-backend-zero.mdc](.cursor/rules/r2-backend-zero.mdc) |

---

## 2. Why “Always-Applied” Matters

Both rules have **`alwaysApply: true`**. Assume they apply for Vert.x/Zero backend work; do not contradict them. When in doubt, open the .mdc via the index.

---

## 3. r2-backend-dbe — DBE Query Syntax (Shared)

**Official description:** Comprehensive guide for Zero Framework DBE (Database Engine) QR Query Syntax. Defines the JSON structure for QQuery (Request) and QTree (Criteria) used in database access.

**Always-applied:** Yes. Applies to any code that builds or interprets DBE requests.

### Summary for AI

- **QQuery:** request DTO with **criteria** (QTree), **pager** (page/size), **sorter** (field,direction), **projection** (fields). Pagination and sorting **outside** criteria.
- **QTree:** Direct (`"field,op": value`), Nested (`"$n": { ... }`), Connector (`"": true` = AND, `"": false` = OR). For OR, set `"": false` explicitly.
- **Operators:** `=`, `<>`, `<`, `<=`, `>`, `>=`; `n`/`!n` (NULL); `i`/`!i` (IN); `s`/`e`/`c` (starts/ends/contains). Map natural language to `op`; keep pager/sorter out of criteria.

---

## 4. r2-backend-zero — Zero Framework DPA (Vert.x)

**Official description:** Definitive guide for DPA (Domain-Provider-API) Backend Architecture. Defines module structure, dependencies, and strict component responsibilities.

**Always-applied:** Yes. Applies to any Zero/Vert.x backend with `*-domain`, `*-provider`, `*-api` modules.

### Summary for AI

- **Modules:** `<project>-domain` (contracts, models, exceptions) ← `<project>-provider` (implementations, DBE) ← `<project>-api` (REST agents, event-bus actors).
- **Component types:** **Agent** = REST endpoint (`*Agent.java`); **Addr** = event address constants (`Addr.java`); **Actor** = event consumer (`*Actor.java`); **Stub** = service interface (`*Stub.java`); **Service** = implementation (`*Service.java`); **DBE** = DB access (jOOQ, `*Dao`).
- **Execution chain:** Agent → Addr → Actor → Stub → Service → DBE. Domain: Table/Stub/Exception; Provider: implements Stub, calls DBE; Api: Addr, Agent, Actor only. **Agent:** no logic; **Actor:** injects Stub, returns Future, no direct DB.
- **Conventions:** Stub/Service/Actor return **`Future<T>`**. Error codes in `domain/exception`. **Transactions and business validation only in Service layer;** format validation (e.g. JSR-303) in Api.
- **.r2mo:** Tables/POJOs align with `.r2mo/domain/*.proto`; API with `.r2mo/api/operations/`; requirements with `.r2mo/requirements/`.

---

## 5. How to Use This Document

- **Vert.x/Zero backend tasks:** Apply both DBE and Zero rules. Use this summary for layer, naming, and query shape; open the indexed .mdc for full syntax or examples.
- **Single source of truth:** `.r2mo` (domain, api/operations, requirements). Keep implementation consistent.
