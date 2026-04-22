# Graph Usage Rules

> Load this file when the task needs operational rules for `code-review-graph` in Zero framework work.
> This file owns graph discipline only. It does not own framework topic routing, topology mapping, Flyway behavior, actor startup, or data bootstrap behavior.

## 1. Scope

This file defines how to use `code-review-graph` against `zero-ecotope` without over-trusting graph output.

Use `mcp-integration-map.md` first when the task starts from a broad topic such as `security`, `workflow`, `excel`, `job`, or `zero-overlay`.
Use `graph-noise-rules.md` when top graph results are dominated by generated assets, bundled JavaScript, Obsidian plugin data, or test fixtures.

## 2. Core Contract

- Use `code-review-graph` as the first structural lookup tool for framework reading.
- Start with `get_minimal_context`.
- Use `semantic_search_nodes` for symbol lookup.
- Use `query_graph` for callers, callees, importers, and file summaries.
- Use `get_impact_radius`, `detect_changes`, and `get_affected_flows` only after the owning layer is known.
- Always pass the explicit `repo_root` for `zero-ecotope`.
- Treat graph output as candidate structure, not as final proof.

One-line rule:

```text
Use the graph to locate ownership and structure, not to infer final semantics.
```

## 3. When Source Verification Is Mandatory

Stop at graph results only if the task is purely structural.

Read source and resources directly when behavior depends on:

- `@Actor`, `@Address`, `@Agent`, `@Queue`
- `META-INF/services/*`
- YAML / JSON / SQL resources
- Flyway migration locations
- plugin resource trees such as `plugins/{MID}/security/*`

Rule:

```text
Graph narrows the search space. Source and resources prove the behavior.
```

## 4. Resource-Driven Reading Rule

Use text search and direct file reads for resource-driven behavior.

The graph is strongest on:

- Java classes
- call relationships
- import relationships
- ownership candidates

The graph is weaker on:

- SPI registration files
- boot metadata
- migration trees
- app resource trees

## 5. Execution Order

Use this order unless a narrower workflow already applies:

1. `get_minimal_context`
2. `semantic_search_nodes`
3. `query_graph`
4. direct file reads for returned source candidates
5. direct file reads for resource trees
6. impact / flow tools only after the owner is confirmed

## 6. Evidence Pattern

When answering with graph-assisted findings, provide:

```text
Graph evidence:
- tool + target

Source evidence:
- verified class/file

Resource evidence:
- verified resource path
```

## 7. Baseline and Refresh

Current target repository:

```text
Alias: mxt-zero
Repo:  /Users/lang/zero-cloud/app-zero/zero-ecotope
```

Current graph status was verified against:

```text
Files:   3932
Nodes:   63217
Edges:   351090
Branch:  master
Built at commit: 28f4a7a4c078
Workspace HEAD:  9775e79896c5f5cdc22b305dcb067f43ef14c64f
```

Refresh commands:

```bash
code-review-graph status --repo /Users/lang/zero-cloud/app-zero/zero-ecotope
code-review-graph update --repo /Users/lang/zero-cloud/app-zero/zero-ecotope
code-review-graph build --repo /Users/lang/zero-cloud/app-zero/zero-ecotope
```

Use `update` for ordinary same-branch edits.
Use `build` after topology changes, broad merges, or when impact analysis depends on stale metadata.

## 8. Practical Caveats

- `semantic_search_nodes` works best with one symbol or class name per query.
- Do not bundle multiple unrelated names into one graph query and expect stable ranking.
- `callers_of` and `callees_of` can be polluted by repository JavaScript bundles when the target name is overly generic such as `run`.
- Prefer fully qualified targets or file-scoped verification for common method names.
- Apply `graph-noise-rules.md` before treating large communities as framework architecture.

Rule:

```text
If graph baseline and workspace HEAD differ, use the graph for orientation only and rely on direct source reads for final proof.
```
