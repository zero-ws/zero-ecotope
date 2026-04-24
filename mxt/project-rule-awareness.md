# Project Rule Awareness

This document defines how AI agents should treat project-local rule files when working in a Zero / Vert.x-first framework project.

## 1. Project-Local Rules Come Before Framework Rules

If project-local rule files exist, inspect them before starting implementation.

Typical project-local sources:

- project `CLAUDE.md` when present
- project `AGENTS.md` when present
- project `CODEX.md` when present
- `.cursor/rules/*.md`
- `.trae/rules/*.md`
- `.r2mo/`

Rule:

```text
`AGENTS.md` is optional, not mandatory. Read it when present; do not assume MCP requires it.
```

## 2. `devapi.md` Still Has Priority When It Exists

If `devapi.md` or an equivalent backend execution rule exists, read it before:

- writing new backend transport
- deciding between CRUD and handwritten DPA
- inventing new API-side behavior

## 3. Framework Rules Are Second Layer, Not Replacement

Framework packs such as `mxt/*` or global framework rules provide shared ownership guidance.
They do not override project-local contracts.

Use them after local rule inspection, not instead of it.

## 4. `.r2mo/` Is Still Contract Truth

Even when local Markdown rule files are absent, `.r2mo/` remains the first contract source for:

- requirement definitions
- model and module intent
- API contract ownership

## 5. Working Rule

Use this sequence:

1. project-local rule files that actually exist
2. project `.r2mo/`
3. framework-level `mxt/*`
4. source verification

## 6. Scope Rule

Always treat project-local rules as scoped to the current project, not as universal framework truth.
