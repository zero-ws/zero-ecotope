# Zero Agent Quick Start

> One-page quick start for AI agents reading Zero from any downstream project.
> Read this first, then jump into the longer framework docs only as needed.

## 1. First Decision

Before reading code, answer two questions:

1. Is the active project really **Zero-first** (`zero-0216`)?
2. Is the task **framework-related**, not just app-local?

If yes, continue.
If no, stay in the downstream project context.

## 2. Minimal Reading Order

Use this order:

```text
project local rules/specs
-> mxt/framework-map.md
-> mxt/search-hints.md
-> mxt/mcp-code-review-graph-rules.md
-> framework source
```

Add these only when needed:

- `plugin-layer-map.md` -> plugin capability questions
- `spi-registry-map.md` -> SPI / Ex* / Sc* / Ui* family questions
- `spi-implementation-rules.md` -> SPI implementation and discovery questions
- `exmodule-boundary.md` -> plugin vs exmodule vs app placement
- `backend-dpa-guide.md` -> DPA and async backend questions
- `dbe-query-rules.md` -> DBE / `QQuery` / `QTree` questions
- `backend-model-tenant-rules.md` -> metadata and tenant questions
- `backend-rbac-rules.md` -> RBAC resource questions
- `crud-engine-guide.md` -> standard CRUD vs handwritten implementation decisions
- `contract-source-rules.md` -> `.r2mo` contract authority questions
- `project-rule-awareness.md` -> project MDC interaction

## 3. The Ownership Ladder

Always classify the requirement into one layer first:

```text
runtime   -> zero-epoch
boot      -> zero-boot
plugin    -> zero-plugins-equip
extension -> zero-extension-skeleton / zero-extension-crud / zero-extension-api
exmodule  -> zero-exmodule-*
app       -> downstream project only
```

One-line rule:

```text
Do not change a lower or higher layer before proving ownership.
```

## 4. Translate Business Words into Framework Words

Downstream projects speak business language.
Zero framework reading requires framework language.

Common translations:

| Business phrasing | Zero phrasing |
|---|---|
| standard list/detail/edit | CRUD engine / `entity.json` / `column.json` |
| search/filter/page/sort | DBE / `QQuery` / `QTree` / `criteria` / `pager` / `sorter` |
| extension hook | `SPI` / `Ex*` / `Sc*` / `Ui*` / `META-INF/services` |
| permission/resource | `RBAC_RESOURCE` / `RBAC_ROLE` / `PERM.yml` / `seekSyntax` |
| cache/session/jwt/ldap | plugin capability |
| startup/config/env issue | `ConfigMod` / `vertx.yml` / runtime contract |
| frontend dynamic form/visibility | `UiForm` / `UiValve` / `zero-ui/src/extension/` |

## 5. Graph Rule

Use `code-review-graph` to find structure, not truth by itself.

Always:

- pass explicit `repo_root` for `zero-ecotope`
- filter backend results to `zero-ecosystem/`
- filter frontend extension results to `zero-ui/src/extension/`
- ignore `.obsidian/plugins` noise unless the task is about repo tooling

Use graph for:

- symbol ownership
- callers/callees/imports
- impact radius
- flow tracing
- architecture clustering

Then open source files.

## 6. Resource Rule

If the task is driven by metadata or resources, graph is not enough.

Do not stop at graph results for:

- `PERM.yml`
- `RBAC_RESOURCE` / `RBAC_ROLE`
- `entity.json` / `column.json`
- `vertx.yml`
- Flyway SQL / DDL / seed data
- resource trees under `plugins/` and `src/main/resources/`

One-line rule:

```text
Graph narrows the owner; files decide the semantics.
```

## 7. Default Query Paths

Use these default paths first:

| Task type | First place to look |
|---|---|
| runtime/request flow | `zero-epoch/`, then `zero-boot/` |
| plugin capability | `zero-plugins-equip/zero-plugins-*` |
| extension contract | `zero-extension-skeleton/` |
| CRUD behavior | `zero-extension-crud/` + metadata resources |
| reusable business module | `zero-exmodule-*` |
| frontend extension | `zero-ui/src/extension/` |
| env/runtime config | `zero-epoch-setting/`, `ConfigMod`, `vertx.yml` |

## 8. Final Check Before Answering

Answer these before you conclude:

1. Which layer owns it?
2. Is this code-driven, resource-driven, or both?
3. Did I verify source files, not only graph results?
4. If resources matter, did I open them directly?
5. Is the conclusion reusable across downstream projects, or only valid for this one app?

## 9. Next Document

After this quick start, the normal next file is:

- `mxt/mcp-code-review-graph-rules.md`

Use that file as the full protocol.
