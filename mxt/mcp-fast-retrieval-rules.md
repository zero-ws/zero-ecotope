# MCP Fast Retrieval Rules

> Load this file when an AI Agent or MCP consumer needs the shortest path from a user topic to source proof.
> This file owns retrieval discipline only. It does not own framework semantics, document distillation, or rule purification.

## 1. Purpose

The goal is minimum token retrieval with enough evidence to act.

One-line rule:

```text
Route once, query once, open only the owner and proof files.
```

## 2. Three-Step Retrieval Path

Use this path for most tasks:

1. route the topic in `mcp-integration-map.md`
2. open only the `Read First` owner document
3. use the listed source anchor or graph hint to verify source

Do not read README, framework map, graph playbooks, and multiple owner documents by default.

If the task is explicitly cross-repository across Momo, Spec, R2MO, and Zero, start with `biological-network-overview.md` and then continue into `biological-network-static-lookup.md`.

## 3. Stop Conditions

Stop retrieving when all are true:

- owner layer is known
- owner document has been read
- one source or resource anchor has been verified
- no cross-layer behavior is suspected

Continue only when:

- the owner document explicitly points to a companion document
- source evidence crosses module boundaries
- resource semantics are required
- the task asks for impact or flow analysis
- the cross-repository handoff itself must be made explicit

## 4. Fast Path Table

| User Topic Shape | First File | Next Action |
|---|---|---|
| broad framework topic | `mcp-integration-map.md` | read mapped owner only |
| unknown symbol | `mcp-integration-map.md` | run one-symbol graph search |
| source path already known | matching owner doc | open source directly |
| resource path already known | matching owner doc | open resource directly |
| graph operation question | `graph-usage-rules.md` | use one playbook only if needed |
| duplicate or mixed rule | `purification-rules.md` | identify true owner |
| document too verbose | `distillation-rules.md` | compress to anchors and decisions |

## 5. Token Saving Rules

- Prefer exact symbol queries over broad semantic queries.
- Query one symbol at a time.
- Prefer file summaries over wide graph traversal.
- Prefer `rg` for resource trees and metadata.
- Do not load all `mxt/*.md` files.
- Do not open companion documents unless the current document names them as required.

## 6. Retrieval Escalation

Escalate in this order:

1. `mcp-integration-map.md`
2. owner document
3. source or resource anchor
4. `graph-usage-rules.md`
5. one playbook section in `mcp-code-review-graph-rules.md`
6. impact or flow graph tools

Exception:

```text
When one repository/layer is already plausible and the remaining uncertainty is structural, a direct code-review-graph deep query is allowed before opening more route docs.
```

Rule:

```text
Graph expansion is the last step, not the entry point.
```

## 7. Context Budget Guidelines

### 7.1 Token Allocation

When an AI agent consumes MXT alongside downstream project context:

| Context Share | Content | Max Files |
|---|---|---|
| ≤ 5% | Decision entry | 1 (`ai-decision-tree.md`) |
| ≤ 10% | Owner documents | 1–2 specific owner guides |
| ≤ 5% | Source anchor verification | 1–2 source files |
| ≥ 80% | Downstream project and conversation | — |

Total MXT consumption should not exceed **15%** of the context window.

### 7.2 Stop-Reading Thresholds

Stop reading MXT when any threshold is reached:

- **Owner known**: the layer and module are identified.
- **Owner doc read**: one owner document has been fully consumed.
- **One anchor verified**: a source or resource file confirms the finding.
- **No cross-layer signal**: the behavior does not span multiple framework layers.
- **Token budget**: cumulative MXT reading exceeds 15% of context.

### 7.3 Parallel Project + Framework Reading

When working in a downstream project that references Zero:

1. Read project-local rules first (`CLAUDE.md` / `AGENTS.md` / `CODEX.md` when present, `.r2mo/`, project rule files).
2. Use `ai-decision-tree.md` as the only MXT entry point.
3. Load at most one owner document per framework concern.
4. Verify with direct source reads, not graph traversal.
5. Never load `README.md`, `framework-map.md`, and `mcp-integration-map.md` together unless the task is explicitly about MXT pack maintenance.

### 7.4 Escalation Budget

If the first owner document does not resolve the question:

- Escalate to one companion document (named in the owner doc).
- Escalate to one graph query (one symbol, one operation).
- Stop after two escalation levels and ask the user for clarification.

## 8. AI Agent Rules

- Read the smallest file that can answer the current question.
- Treat tables as routing contracts.
- Treat owner documents as source-entry contracts.
- Treat graph output as a locator, not final proof.
- Return the source anchor used so the next agent can resume without re-reading.
- Respect the 15% context budget for MXT consumption.
