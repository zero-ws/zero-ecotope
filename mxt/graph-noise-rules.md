# Graph Noise Rules

> Load this file when `code-review-graph` output is polluted by generated assets, bundled JavaScript, Obsidian plugin data, test fixtures, or other non-framework nodes.
> This file owns graph-noise filtering only. It does not own graph query playbooks, framework topic routing, or source semantics.

## 1. Purpose

Graph noise rules keep MCP retrieval focused on framework code.

One-line rule:

```text
Filter graph output by framework ownership before trusting rank or community size.
```

## 2. Known Noise Sources

Treat these as non-authoritative for Zero framework semantics unless the task explicitly targets them:

- `.r2mo/.obsidian/`
- `.obsidian/plugins/`
- `src/main/resources/swagger-ui/`
- generated or bundled JavaScript assets
- test fixture resources
- `target/`
- `.flattened-pom.xml`

These paths can dominate graph communities and semantic rankings because they contain many generated symbols.

## 3. Preferred Include Roots

For framework MCP work, prefer these roots:

- `zero-ecosystem/zero-epoch/`
- `zero-ecosystem/zero-boot/`
- `zero-ecosystem/zero-plugins-equip/`
- `zero-ecosystem/zero-plugins-extension/`
- `zero-version/`
- `mxt/`

Use `zero-ui/` only when the task explicitly targets UI framework work.

## 4. Query Filtering Rules

Before using graph output as evidence:

1. discard result paths under known noise sources
2. prefer Java source paths over generated resource bundles
3. prefer module POMs and source anchors over community size
4. verify the selected owner in `mcp-integration-map.md`
5. open the source or resource file directly

Rule:

```text
Graph rank is not ownership. Path ownership is stronger than score.
```

## 5. Community Interpretation

Large communities are not automatically important.

Examples:

- Swagger UI bundled JavaScript can dominate `zero-plugins-swagger` community size.
- Obsidian plugin assets can appear as top communities even though they are repository tooling noise.
- Generated DAO classes can cluster as large persistence communities but still require owner-module verification.

Use communities for orientation, not final coverage decisions.

## 6. AI Agent Rules

- Do not route framework questions from `.obsidian` or bundled JS results.
- Do not infer missing framework rules from test-only communities.
- Do not treat generated DAO volume as a standalone rule owner.
- If graph output is noisy, fall back to POM/module diff plus exact symbol search.
- Record which noisy paths were ignored when the final answer depends on graph audit.
