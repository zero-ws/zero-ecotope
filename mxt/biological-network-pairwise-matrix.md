# Biological Network Pairwise Matrix

> Pairwise processing matrix for the four framework repositories.
> Use this file when the task should be closed by one repository pair instead of a full cross-repository sweep.

## 1. Purpose

The biological network is pairwise-first.

This file defines the six stable repository pairs:

- `rachel-momo` <-> `r2mo-rapid`
- `rachel-momo` <-> `zero-ecotope`
- `rachel-momo` <-> `r2mo-spec`
- `r2mo-spec` <-> `r2mo-rapid`
- `r2mo-spec` <-> `zero-ecotope`
- `r2mo-rapid` <-> `zero-ecotope`

One-line rule:

```text
Pick the smallest repository pair that can close governance, semantics, runtime, and proof.
```

## 2. Pair Selection Table

| Repository pair | Use when | First repo | Second repo | Typical stop condition |
|---|---|---|---|---|
| `momo` + `r2mo` | version/BOM question plus Spring runtime proof | `rachel-momo` | `r2mo-rapid` | version source and Spring owner both proven |
| `momo` + `zero` | version/BOM question plus Zero runtime proof | `rachel-momo` | `zero-ecotope` | version source and Zero owner both proven |
| `momo` + `spec` | version/governance affects shared contract exposure | `rachel-momo` | `r2mo-spec` | BOM exposure and shared contract surface both proven |
| `spec` + `r2mo` | shared meaning plus Spring execution | `r2mo-spec` | `r2mo-rapid` | contract meaning and Spring runtime proof both proven |
| `spec` + `zero` | shared meaning plus Zero delivery/runtime proof | `r2mo-spec` | `zero-ecotope` | contract meaning and Zero runtime proof both proven |
| `r2mo` + `zero` | runtime comparison, shared capability split, or cross-stack owner check | whichever signal is stronger | the other runtime repo | runtime split is proven without needing governance/spec |

## 3. Pairwise Reading Paths

### 3.1 `rachel-momo` + `r2mo-rapid`

Use for:

- Spring baseline drift
- parent/BOM questions that affect Spring modules
- AspectJ governance plus Spring AOP runtime follow-up

Reading path:

```text
consumer-agent-rules.md or version-governance.md
-> stack-bom-map.md or aspectj-governance.md
-> spring-aop-guide.md or matching r2mo route doc
-> exact POM proof + exact source proof
```

### 3.2 `rachel-momo` + `zero-ecotope`

Use for:

- Zero baseline drift
- parent/BOM questions that affect Zero extension or plugin behavior
- dependency alignment plus Zero runtime ownership

Reading path:

```text
version-governance.md
-> stack-bom-map.md
-> biological-network-static-lookup.md or ai-decision-tree.md
-> one Zero owner guide
-> exact POM proof + exact source/resource proof
```

### 3.3 `rachel-momo` + `r2mo-spec`

Use for:

- whether a shared contract is exposed by the governed baseline
- whether an upgrade changes the shared spec surface

Reading path:

```text
version-governance.md
-> stack-bom-map.md
-> spec-boundary.md
-> model-doc-protocol.md or interface-semantics.md
-> exact POM proof + exact spec proof
```

### 3.4 `r2mo-spec` + `r2mo-rapid`

Use for:

- shared markers/models used by Spring runtime
- contract-first questions that later require execution proof
- AOP wording mixed with shared payload meaning

Reading path:

```text
spec-boundary.md
-> search-hints.md
-> spring-aop-guide.md or matching r2mo route doc
-> exact spec proof + exact Spring source proof
```

### 3.5 `r2mo-spec` + `zero-ecotope`

Use for:

- shared markers/models used by Zero exmodules
- CRUD/UI/MBSE modeling chains
- shared contract meaning plus Zero delivery/runtime proof

Reading path:

```text
spec-boundary.md
-> search-hints.md
-> static-modeling-guide.md or matching Zero owner guide
-> exact spec proof + exact Zero source/resource proof
```

### 3.6 `r2mo-rapid` + `zero-ecotope`

Use for:

- R2MO vs Zero ownership split
- shared capability vs Zero exmodule delivery split
- AOP split between Spring and Zero
- IO/DBE/runtime comparison without mandatory Momo or Spec involvement

Reading path:

```text
framework-trigger-matrix.md or ai-decision-tree.md
-> one runtime owner guide
-> biological-network-cross-repo-handoff.md
-> partner runtime owner guide
-> exact source/resource proof
```

## 4. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is allowed inside a pair when:

- both repositories in the pair are already known
- the unresolved part is structural
- one query can land on the owner symbol or impact surface faster than reading more docs

Examples:

- `spec` + `zero`: find all Zero consumers of one marker-backed schema
- `momo` + `r2mo`: locate the Spring module actually consuming a governed dependency
- `r2mo` + `zero`: compare AOP owners for one hook name

## 5. Optional Third Repository Rule

A third repository is optional only when the pair still cannot close the question.

Typical reasons:

- the pair proves runtime behavior but not shared meaning
- the pair proves shared meaning but not governed exposure
- the pair proves one runtime line but the user explicitly asks for cross-runtime comparison plus governance

Rule:

```text
Add a third repository only to solve one unresolved point, never for broad background loading.
```

## 6. Pairwise Output Contract

Return:

```text
Chosen pair:
Why this pair is sufficient:
Repo A proof:
Repo B proof:
Direct graph retrieval used:
Optional third repo needed:
```

## 7. Companion Documents

- `biological-network-overview.md`
- `biological-network-static-lookup.md`
- `biological-network-dynamic-lookup.md`
- `biological-network-cross-repo-handoff.md`
