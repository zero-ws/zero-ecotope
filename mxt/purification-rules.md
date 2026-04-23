# Purification Rules

> Load this file when cleaning duplicate, overlapping, or mixed-responsibility MXT rules.
> This file owns rule purification only. It does not own framework facts, topic routing, or graph operations.

## 1. Purpose

Purification means keeping one authoritative owner for each rule.

One-line rule:

```text
One rule, one owner document, one shortest route.
```

## 2. Owner Precedence

When the same rule appears in multiple places, use this precedence:

1. topic owner document owns source semantics
2. `mcp-integration-map.md` owns topic routing
3. `mcp-fast-retrieval-rules.md` owns shortest retrieval discipline
4. `graph-usage-rules.md` owns graph safety
5. `mcp-code-review-graph-rules.md` owns graph playbook sequences
6. `search-hints.md` owns text-search anchors
7. `README.md` owns pack entry and index

If two documents both explain the same behavior, keep the explanation in the higher-specificity owner and leave only a route in the other.

## 3. Duplicate Handling

Use these actions:

| Duplicate Type | Action |
|---|---|
| same rule, same owner | merge into one statement |
| same rule, different owner | keep in true owner, replace other copy with a link |
| similar rule, different layer | split by layer |
| broad rule hiding several topics | split into owner documents |
| outdated wording | replace, do not preserve both versions |

Do not delete useful intent. Upgrade it into the correct owner.

## 4. Mixed Responsibility Smells

Split or rewrite when a document mixes:

- routing plus source semantics
- graph safety plus framework facts
- source ownership plus resource authoring rules
- plugin capability plus exmodule business behavior
- startup sequencing plus one actor's internal logic

Rule:

```text
If the reader needs a different first search anchor, it is probably a different document.
```

## 5. Purification Checklist

Before finalizing a rule change, verify:

1. the owner document is obvious
2. the same rule is not repeated elsewhere
3. non-owner documents route instead of explain
4. the route reaches source anchors in one hop
5. the rule is written in English and agent-readable

## 6. AI Agent Rules

- Do not add a rule to README unless it is an entry rule.
- Do not add graph procedure to `mcp-integration-map.md`.
- Do not add source semantics to `search-hints.md`.
- Do not add broad framework background to owner documents.
- When in doubt, choose the file that owns the first search anchor.
