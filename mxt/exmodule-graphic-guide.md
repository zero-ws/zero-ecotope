# Exmodule Graphic Guide

> Load this file when the task is about graph/topology business semantics in `zero-exmodule-graphic`.

## 1. Scope

`zero-exmodule-graphic` owns reusable graphic/graph business meaning above raw graph capability.

## 2. Verified Anchors

- API:
  - `GraphActor`
- Provider:
  - `MDGraphicActor`
  - `ExtensionGraphicSource`

## 3. Boundary

Use Graphic when the issue is about domain-level graph semantics, topology records, or graph business assets.

Do not use Graphic for:

- raw Neo4j/client/driver capability
- infrastructure graph storage wiring

That belongs with `neo4j-guide.md` or plugin-layer documents.

## 4. Source and Resource Path

Read in this order:

```text
exmodule-graphic-guide.md
-> neo4j-guide.md only if storage/client ownership is unclear
-> zero-exmodule-graphic source/resources
```

High-value proof targets:

- `GraphActor`
- `MDGraphicActor`
- `ExtensionGraphicSource`
- module-owned model or topology resources

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for graphic business semantics
- `zero-ecotope` + `r2mo-spec` when shared graph/topology nouns must be confirmed as contracts

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- `GraphActor` or `MDGraphicActor` is already known
- the unresolved point is structural ownership between exmodule business logic and Neo4j capability
