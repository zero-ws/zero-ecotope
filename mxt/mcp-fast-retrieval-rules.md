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

Rule:

```text
Graph expansion is the last step, not the entry point.
```

## 7. AI Agent Rules

- Read the smallest file that can answer the current question.
- Treat tables as routing contracts.
- Treat owner documents as source-entry contracts.
- Treat graph output as a locator, not final proof.
- Return the source anchor used so the next agent can resume without re-reading.
