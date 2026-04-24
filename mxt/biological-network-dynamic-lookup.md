# Biological Network Dynamic Lookup

> Dynamic lookup path for AI agents when structural depth is needed across the four framework repositories.
> This file defines when and how to escalate into graph-backed or direct deep code retrieval.

## 1. Dynamic Lookup Goal

Dynamic lookup answers:

- where a symbol is implemented
- who calls it
- what changes with it
- whether another repository also owns part of the behavior

One-line rule:

```text
Dynamic lookup starts only after a plausible owner exists or when one targeted deep probe is cheaper than more broad reading.
```

## 2. Deep Retrieval Modes

### 2.1 Owner-guided graph lookup

Default dynamic mode:

```text
owner doc -> graph query -> source/resource proof
```

Use when the owner repository and layer are already plausible.

### 2.2 Direct deep repository lookup

Allowed exception:

```text
signal -> direct code-review-graph or exact source search -> owner confirmation -> proof
```

Use this only when:

- the signal already points to one repository or one code family
- the unresolved part is structural
- a graph query will reduce reading cost immediately

Examples:

- known class or annotation names
- known module prefixes
- known AOP advice names
- changed-file impact review

The same rule applies in pairwise mode.
If one repository or one repository pair is already enough, do not expand the search to outside repositories.

## 3. Dynamic Tool Priority

### 3.1 Zero

Prefer:

1. `graph-usage-rules.md`
2. `mcp-code-review-graph-rules.md`
3. `code-review-graph` or graph MCP queries
4. direct source/resources

### 3.2 R2MO

Prefer:

1. `code-review-graph-usage.md`
2. `mcp-route-code-review-graph.md`
3. `code-review-graph`
4. direct source

### 3.3 Spec

Prefer direct source over graph:

1. `search-hints.md`
2. exact metadata/OpenAPI reads

Only use graph if a consumer/runtime repository references a spec symbol and ownership spread must be traced outside `r2mo-spec`.

### 3.4 Momo

Prefer POM reading over graph:

1. `consumer-agent-rules.md`
2. `pom-analysis.md`
3. exact XML search

Graph is optional and low priority here.

## 4. Direct `code-review-graph` Rule

Direct `code-review-graph` repository retrieval is valid in necessary scenarios.

Recommended cases:

- symbol ownership is uncertain between Zero and R2MO
- call flow or impact radius matters
- one module family is known but the exact source anchor is not
- the task is a framework review instead of a semantic definition task

Not recommended:

- pure version-governance questions
- pure marker/metadata meaning questions
- resource-only questions where the answer lives in YAML, JSON, SQL, or `META-INF/services`

Mandatory fallback:

```text
If graph and source disagree, trust source/resources and refresh or narrow the graph query.
```

## 5. Graph Query Decision Table

| Dynamic need | Best first move |
|---|---|
| known symbol, unknown owner | one-symbol `code-review-graph` search |
| known owner, unknown callers | `callers_of` or equivalent graph lookup |
| changed files, unknown blast radius | impact/detect-changes query |
| route/API/event flow | flow-oriented graph query then source verification |
| resource-heavy behavior | direct resource read first |

## 6. Dynamic AOP Lookup

### 6.1 Spring-side AOP

Use:

```text
r2mo-rapid -> spring-aop-guide.md -> code-review-graph symbol lookup -> aspect source -> advice target proof
```

### 6.2 Zero-side AOP

Use:

```text
zero-ecotope -> extension-aop-guide.md -> graph lookup -> advice source -> exmodule/plugin proof
```

### 6.3 AspectJ governance

Use:

```text
rachel-momo -> aspectj-governance.md -> root or module pom proof
```

Do not use runtime graph lookup as the first step.

## 7. Cross-Repo Escalation

Escalate to another repository when dynamic lookup finds:

- shared markers or metadata referenced from runtime code
- version/governance constraints affecting runtime choices
- Zero and R2MO both consuming the same contract

Use `biological-network-cross-repo-handoff.md` to transfer context.

Prefer escalation by pairs:

1. current repository
2. one necessary partner repository
3. stop if proof is closed
4. add another repository only if the current pair still cannot resolve ownership or semantics

## 8. Minimal Evidence Pattern

Return findings in this order:

```text
Owner hypothesis:
Graph or deep retrieval:
Source proof:
Resource proof:
Cross-repo handoff:
```

## 9. Final Dynamic Rule

```text
Direct deep retrieval is allowed when it saves time, but it never replaces owner classification and proof verification.
```
