# Distillation Rules

> Load this file when adding, shrinking, or rewriting MXT documents for AI Agent consumption.
> This file owns document distillation only. It does not own topic routing, source ownership, or graph playbooks.

## 1. Purpose

Distillation means turning framework facts into the smallest reusable agent-readable form.

One-line rule:

```text
Keep the decision path, the owner, and the proof anchor; remove everything else.
```

## 2. Distilled Document Shape

Every owner document should prefer this shape:

1. scope
2. owning module or resource tree
3. verified source anchors
4. responsibility model
5. boundary rules
6. AI Agent rules

Do not add background narrative unless it changes a placement or lookup decision.

## 3. Token Budget Rule

Use the smallest stable form:

| Document Type | Target Shape |
|---|---|
| routing document | compact table |
| owner document | short sections plus anchor list |
| playbook document | numbered procedure |
| audit document | table plus conclusion |
| evolution document | trigger and required sync list |

Avoid:

- repeated framework history
- long prose summaries
- duplicated source listings
- examples that do not change the rule
- broad explanations already owned by another document

## 4. Evidence Compression

Use anchors instead of paragraphs.

Prefer:

```text
Verified anchors:
- ExBoot
- SPI_SET
- HPI.findMany
```

Avoid:

```text
Long explanation of how the whole boot system works...
```

The agent can follow anchors with MCP, graph, or text search when deeper proof is needed.

## 5. Stop Rule

Stop writing when the document answers:

1. when to load it
2. what it owns
3. what it does not own
4. where to search first
5. what not to confuse it with

If more content is needed, create or update a different owner document instead of expanding the current one.

## 6. AI Agent Rules

- Prefer lists and tables over prose.
- Prefer exact file, module, class, annotation, and resource names.
- Keep each paragraph short enough to be independently retrievable.
- Put source anchors near the top.
- Do not duplicate another document's rule; link to it by filename.
