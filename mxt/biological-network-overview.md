# Biological Network Overview

> Cross-repository MD-driven thinking network for AI agents working across `zero-ecotope`, `r2mo-rapid`, `r2mo-spec`, and `rachel-momo`.
> This file defines the system model only. It does not replace owner documents.

## 1. Purpose

The goal is to let an AI agent route broad wording into the correct repository, owner document, source anchor, and graph query with minimal drift.

This network is built with markdown only.

The network is not all-or-nothing.
Any two repositories must be able to form a complete working path by themselves.
Dependencies on the other repositories are optional escalation paths, not mandatory prerequisites.

It does not require:

- a mandatory repository-root `AGENTS.md`
- a separate database beyond optional `code-review-graph`
- custom runtime code inside the four repositories

One-line rule:

```text
Use markdown to route intent, use graph tooling to deepen lookup, use source/resources to prove the answer.
```

## 2. Biological Layer Mapping

Use the metaphor only as an operational shortcut:

| Biological term | Meaning in this network |
|---|---|
| signal | user wording, symbols, errors, file paths, module names |
| neuron | one routing document or trigger table |
| tissue | one repository-local document family such as Zero exmodule guides or R2MO route docs |
| organ | one repository with a stable responsibility boundary |
| reflex | shortest-path lookup without graph expansion |
| cortex | deliberate deep lookup through graph queries and source verification |
| circulation | cross-repository handoff with an explicit output contract |

## 3. Repository Organs

### 3.1 `zero-ecotope`

Owns Vert.x-first runtime, boot wiring, plugin capability ownership, extension layers, and exmodule runtime behavior.

Use it for:

- `zero-extension-*`
- `zero-exmodule-*`
- Zero plugin/runtime behavior
- Zero-side AOP and before/after hook behavior

### 3.2 `r2mo-rapid`

Owns Spring-first runtime, shared capability implementations, Spring integrations, delivery modules, and Spring-side AOP/runtime assembly.

Use it for:

- `r2mo-spring-*`
- `r2mo-boot-*`
- `r2mo-dbe*`, `r2mo-io`, `r2mo-jaas`, `r2mo-jce`
- Spring Security and Spring AOP

### 3.3 `r2mo-spec`

Owns shared semantic contracts only.

Use it for:

- marker semantics
- metadata model meaning
- shared OpenAPI fragments

Do not use it as the owner for:

- AOP
- runtime execution order
- Spring or Vert.x container behavior

### 3.4 `rachel-momo`

Owns dependency, BOM, and parent-governance semantics above runtime lines.

Use it for:

- version alignment
- BOM exposure
- parent inheritance
- AspectJ dependency/plugin governance

Do not use it as the owner for runtime advice execution.

## 4. Two Retrieval Modes

### 4.1 Static lookup

Use when the task starts from broad wording, known modules, or repository-local responsibility questions.

Path:

```text
signal -> repo organ -> route doc -> owner doc -> source/resource proof
```

Primary documents:

- `biological-network-static-lookup.md`
- `biological-network-node-map.md`
- repository `README.md`

### 4.2 Dynamic lookup

Use when ownership is plausible but structure, impact radius, call flow, or actual implementation spread is still unclear.

Path:

```text
signal -> plausible owner -> graph query or direct source search -> proof -> cross-repo handoff if needed
```

Primary documents:

- `biological-network-dynamic-lookup.md`
- `biological-network-cross-repo-handoff.md`
- repository-local graph usage rules

## 5. Direct Deep Retrieval Rule

The default entry is still the owner-document route.

But direct `code-review-graph` lookup is allowed when all are true:

- the user wording is already close to one repository/layer
- the remaining uncertainty is structural, not semantic
- reading more owner docs would cost more than one targeted graph query

Typical examples:

- locate all callers of a known AOP advice class
- inspect exmodule blast radius after a local change
- confirm whether a symbol belongs to Zero, R2MO, or shared contracts

Mandatory fallback:

```text
Graph result narrows the field. Source and resources still decide the conclusion.
```

## 6. Cross-Repository Baseline

Use this ownership ladder:

1. `rachel-momo` for version and build governance
2. `r2mo-spec` for shared meaning and standard contracts
3. `r2mo-rapid` for Spring-first runtime and shared capability implementation
4. `zero-ecotope` for Vert.x-first runtime, Zero plugins, extension runtime, and exmodule delivery

Rule:

```text
Meaning first, runtime second, delivery last, local workaround never before framework ownership.
```

## 6.1 Pairwise Processing Rule

Treat the four repositories as a network of optional pairings, not a fixed four-step pipeline.

Required property:

- any two repositories should be enough to finish one concrete reasoning chain

Examples:

- `rachel-momo` + `r2mo-rapid` for version governance plus Spring runtime proof
- `rachel-momo` + `zero-ecotope` for baseline drift plus Zero runtime proof
- `r2mo-spec` + `r2mo-rapid` for shared contract meaning plus Spring execution
- `r2mo-spec` + `zero-ecotope` for shared contract meaning plus Zero delivery/runtime proof
- `r2mo-rapid` + `zero-ecotope` for runtime comparison without mandatory Momo or Spec involvement

Rule:

```text
Outside repositories are optional enrichments unless the current pair cannot close the unresolved question.
```

## 7. Output Contract

When handing off between repositories, return:

```text
Current signal:
Classified repo:
Classified layer:
Owner document:
Proof anchor:
Graph query used:
Next repository if needed:
Reason for handoff:
```

## 8. Companion Documents

- `biological-network-node-map.md`
- `biological-network-static-lookup.md`
- `biological-network-dynamic-lookup.md`
- `biological-network-cross-repo-handoff.md`
- `biological-network-pairwise-matrix.md`
